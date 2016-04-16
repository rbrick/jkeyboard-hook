Java Virtual Machine still can not catch global key events. This project is the implementation of this possibility.
It would be possible to setup shortcuts like :

CTRL + ALT + **KEY**

SHIFT + **KEY**

ALT + **KEY**

CTRL + SHIFT + ALT + **KEY**

in other words, you can combine CTRL, SHIFT, ALT with another keyboard KEY.

The idea is to use a special native DLL which is able to catch necessary global events, and back them to the Java Virtual Machine via JNI.

Please, see source codes and examples, to understand how to use the library.

The current version of the library has a bottle neck : only shortcut combination, but in the future we would like to extend it to support many combinations at the same time.

This small project is based on the blog post:
http://biletnikov-dev.blogspot.com/2009/09/global-hotkeys-for-java-applications_25.html