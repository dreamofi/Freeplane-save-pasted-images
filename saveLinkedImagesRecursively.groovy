@Grab('org.jsoup:jsoup:1.12.1')
import java.nio.file.*
import java.text.SimpleDateFormat
import org.freeplane.features.map.mindmapmode.clipboard.MMapClipboardController
import org.jsoup.Jsoup;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;

//TODO: Add ability to save SVG as PNG, and also the original SVG file?
//TODO: Add ability to process Base64 image

//Get selected nodes:
def selectedNodes = c.selecteds

//Get list of node containing image links
c.statusInfo = "Processing"
def listNodeWithImg = []
def listNodeWithNotesWithImg = []

selectedNodes.each{ eachNode -> 
   listNodeWithImg += eachNode.find{htmlUtils.isHtmlNode(it.text) && it.text.contains("<img")}
   listNodeWithNotesWithImg += eachNode.find{(it.noteText != null) && it.noteText.contains("<img")}
}

//Get list of node with notes containing image links

//Check if img links in listNodeWithImg is downloadable

/**
 * Http HEAD Method to get URL content type
 *
 * @param urlString
 * @return content type
 * @throws IOException
 */
 
public String getContentType(String urlString) throws IOException{
    URL url = new URL(urlString);
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod("HEAD");
    if (isRedirect(connection.getResponseCode())) {
        String newUrl = connection.getHeaderField("Location"); // get redirect url from "location" header field
        logger.warn("Original request URL: '{}' redirected to: '{}'", urlString, newUrl);
        return getContentType(newUrl);
    }
    String contentType = connection.getContentType();
    return contentType;
}

/**
 * Check status code for redirects
 * 
 * @param statusCode
 * @return true if matched redirect group
*/
protected Boolean isRedirect(int statusCode) {
    if (statusCode != HttpURLConnection.HTTP_OK) {
        if (statusCode == HttpURLConnection.HTTP_MOVED_TEMP
            || statusCode == HttpURLConnection.HTTP_MOVED_PERM
                || statusCode == HttpURLConnection.HTTP_SEE_OTHER) {
            return true;
        }
    }
    return false;
}

def isDownloadable (String link) {
    def imgType = ['image/png','image/jpg','image/jpeg','image/png','image/tiff','image/bmp', 'image/tiff-fx', 'image/gif']
    try {
        def contentType = getContentType(link)
        if (imgType.contains(contentType)){
            return (contentType - 'image/')
        } else {
            return "nope"    
        }
    } catch (Exception e) {
        c.statusInfo = e
        return "nope"
    }
}

def redirectFollowingDownload( String url, String filename ) {
  while( url ) {
    new URL( url ).openConnection().with { conn ->
      conn.instanceFollowRedirects = false
      url = conn.getHeaderField( "Location" )      
      if( !url ) {
        new File( filename ).withOutputStream { out ->
          conn.inputStream.with { inp ->
            out << inp
            inp.close()
          }
        }
      }
    }
  }
}


def downloadFile (String link, String fileSave) {
    def fileExt = isDownloadable(link)
    if (fileExt != "nope"){                
        redirectFollowingDownload(link, fileSave)
    }    
}

def convertFileUri (String fileUri) {
   def file2Copy = ""   
   if (fileUri.contains("file:////")){
       file2Copy = fileUri - "file:///"
       return file2Copy
   } else {
       file2Copy = fileUri - "file://"
       return file2Copy
   }
}

def getDateTimeNow () {
    def date = new Date()
    sdf = new SimpleDateFormat("yyyyMMdd-HHmmss")
    def dateNow = sdf.format(date).toString()            
    return dateNow
}
//TODO: Show popup listing images failed to fetch

/* Parse the text of nodes containing imgs to extract src */

def checkNote(nodeTest, Boolean withNote) {
    if (withNote == false) {
        return nodeTest.text    
    } else {
        return nodeTest.noteText
    }
}

def downloadImages (nodes, Boolean withNote) {
    if (nodes.isEmpty()) {
        c.statusInfo = "Nothing to download"    
    } else {
        
        //Get mm file path
        def fileTitle = map.getName().toString();
        def fileName = map.getFile().toString();
        def imgRelPath = Paths.get(fileTitle + "_files",'img').toString()
        def fileAbsPath = Paths.get(Paths.get(fileName).getParent().toString(),imgRelPath);
        
        //Create mm_files/img folder         
        if (Files.notExists(fileAbsPath)){
            Files.createDirectories(fileAbsPath)
        } 
        
        nodes.each{eachNode -> 
            
            Document parseString = Jsoup.parse(checkNote(eachNode, withNote));    
            
            Elements imgTags = parseString.getElementsByTag('img');

            def index = 0;
            println imgTags
         
            imgTags.each {item -> 
                def imgSrc = item.attr("src")

                //Copy image from tmp to mm folder if it is on local machine
            if (imgSrc.contains("file:////") || imgSrc.contains("file:///")) {
                    def file2Copy = convertFileUri(imgSrc)
                    def srcPath = Paths.get(file2Copy)

                    def baseName = ""
                    if (withNote == false) {
                        baseName = "${eachNode.id}_${index}_${getDateTimeNow()}" + srcPath.getFileName()
                    } else {
                        baseName = "Note_${eachNode.id}_${index}_${getDateTimeNow()}" + srcPath.getFileName()
                    }

                    def desPath = Paths.get(fileAbsPath.toString(), baseName)
                    if (Files.exists(srcPath)){
                        Files.copy(srcPath, desPath) 
                        if (withNote == false) {
                            eachNode.text = eachNode.text.replace(imgSrc, Paths.get(imgRelPath, baseName).toString())            
                            eachNode.link.text = Paths.get(imgRelPath, baseName).toString()
                        } else {
                            eachNode.noteText = eachNode.noteText.replace(imgSrc, Paths.get(imgRelPath, baseName).toString())            
                        }
                        index += 1
                    }
                }       
                               
                //Download image if it is on the internet
                def fileExt = isDownloadable(imgSrc)                
                if (fileExt != "nope") {       
                    def dateNow = getDateTimeNow() 

                    String fileSaveName = ""
                    if (withNote == false){
                        fileSaveName = "${eachNode.id}_${index}_${dateNow}.${fileExt}"
                    } else {
                        fileSaveName = "Note_${eachNode.id}_${index}_${dateNow}.${fileExt}"
                    }

                    def replaceNodePath = Paths.get(imgRelPath, fileSaveName).toString()
                    def fileDownloadPath = Paths.get(fileAbsPath.toString(), fileSaveName).toString()

                    //Download file
                    downloadFile(imgSrc, fileDownloadPath)

                    //Set the node src
                    if (withNote == false){
                        eachNode.text = eachNode.text.replace(imgSrc, replaceNodePath)            
                    } else {
                        eachNode.noteText = eachNode.noteText.replace(imgSrc, replaceNodePath)            
                    }
                    index += 1
                }
            }
           
        }
    }
     
}

downloadImages(listNodeWithNotesWithImg, true)
downloadImages(listNodeWithImg, false)

c.statusInfo = "DONE!"
