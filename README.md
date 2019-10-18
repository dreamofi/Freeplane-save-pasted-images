# Freeplane-save-pasted-images

A groovy script for Freeplane to save images when pasting from web or text/html mime type with file URI

## Introduction

This script will download the images when you copy a selection with mixed text and images from browsers to mapname_files/img folder. It will also copy the files when you make a copy from applications such as Libre Writer, which puts the images into a temp folder and link to it using file URI (file:///) protocol.

All the downloaded images are relatively linked to in the mm file, making it easier to package the mm file and its associated images.

The script was tested in Freeplane 1.7.9, on a PC running MX Linux 18.3 (based on Debian 9 Stretch)

## Installation

You just need to copy the savePastedImages.groovy to Freeplane user scripts folder (on Linux it is ~/.config/freeplane/1.7.x/scripts)
And also download the Jsoup core library from [here](https://jsoup.org/download) (the latest is jsoup-1.12.1.jar) and put it into ~/.config/freeplane/1.7.x/lib

You may also need to enable permission to read, write files and run network operation in Tools/Preferences/Plugins/Scripting

## How to use

After pasting like usual, select the parent node if it is changed, and then run the script from Tools/Scripts/Save Pasted Images
It is best to assign this script to a shortcut
