# Freeplane-save-pasted-images

A collection of Groovy scripts for Freeplane to fetch images when pasting from web or text/html mime type with file URI

## Introduction

These scripts will deal with images linked to external sources, allowing you to save them to local mapname_files/img folder.

- savePastedImages.groovy: download the images when you copy a selection with mixed text and images. It will also copy the files when you make a copy from applications such as Libre Writer, which puts the images into a temp folder and link to it using file URI (file:///) protocol. All the downloaded images are relatively linked to in the mm file, making it easier to package the mm file and its associated images. Doesn't work on notes.

- saveLinkedImagesRecursively.groovy: download all linked images in selected nodes and their children nodes, as well as in their notes (if any). Doesn't work on non-html nodes even if they have "img" tag inside the text.

The scripts were tested in Freeplane 1.7.9, on a PC running MX Linux 18.3 (based on Debian 9 Stretch)

## Installation

You just need to copy all the scripts to Freeplane user scripts folder (on Linux it is ~/.config/freeplane/1.7.x/scripts)

And also download the Jsoup core library from [here](https://jsoup.org/download) (the latest is jsoup-1.12.1.jar) and put it into ~/.config/freeplane/1.7.x/lib

You may also need to enable permission to read, write files and run network operation in Tools/Preferences/Plugins/Scripting

## How to use

Just run the script fromm Tools/Scripts/Save Pasted Images, it will paste the data and download/copy the images automatically
It is best to assign this script to a shortcut

## Limitations

At the moment, the scripts only works with text/html type, and are not able to process SVG or Base64 images or images embed in rtf mime type (e.g. when copying from Word 2010 in WINE, there is only rtf type in the clipboard).

I have yet tested the script on Windows or MacOS.

If you have any ideas or issues, please pm me or make a ticket on github.
