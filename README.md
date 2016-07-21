# ProgressiveImageEditor
The "Progressive Image editor" (or short PIE) is a programm to edit images in a non-destructive way

## About this program
Have you ever used Blender? Then maybe you got fascinated from the Node Editor, like it happened to me. The possibility to connect everything with everything, creating data-flow paths as you wish, is a very nice concept. 

The idea of the ProgressiveImageEditor is roughly the same, just not for rendering output, but for images in general. Connect image-processing nodes in such a way that in the end, your output can be re-generated automatically each time you make a change in the network structure.

I suggest you try it out yourself!

## "Installation"
The first time the program is started, it installs itself on the system (sadly without automatically linking the file extension to itself, see todo-list). That basically means that a directory `%appdata%/brendeer.pie/` is created and the jar copies itself and some other useful files there. So to uninstall the program again, just remove this directory.

## To-Do
 - Display the FilterSettings and mke the filters adjustable
 - work on the plugin api, so that other plugins can be integrated by simply pasting their jars into some dedicated plugins directory
 - make gui look better
 - way more plugins
 - support for image sequences (maybe have a program-global variable and be able to set the image-in-path to `frame_{i}` or something like that)

## Contribution to the project
Yes! If you have any ideas / see some bugs / would like to see some features, then let me know. This is supposed to be open source, so if you feel like helping out, let nobody stop you!