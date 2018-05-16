# GrxSettings V2

This is an application for Mods Configuration and Rom Control, for LP+ 5.1

## Main Features

* 30 types of preferences which can be referenced using their short name. Some of them have several behaviours depending on added xml attributes.
* Backup of preferences values and restoration
* Root support, needed if you use some features. 
* RTL compatible 
* Navigation panel through preferences screens, supporting groups of preferences screens.
* Nested screen keeping the toolbar.
* Floating action button.
* Several themes available. Easy creation of new themes. 
* Up to 7 actions can be run after a preference value change: reboot, restart an app, run a script (file, file+arguments,string*array commands), change a group key, send up to 2 custom broadcasts, send common broadcast with extras and to simulate an onclick event. 
* Customizable dependency rules. Enable or disable preferences based on other preference´s values, even if the preference is not in the same preference screen.
* Customizable Build Prop rules, allowing to hide/show individual preferences, individual preference screens, or groups of preferences screens, based on build prop properties values.
* Full management for standard Secure, Global and System Settings.
* Support for rom info creation, through easily configurable sliding tabs.
* User options: floating area with access to last seen screens, Theme selection, 3 color pickers styles, image selection for navigation panel header background, divider height selection in list views, show/hide and position of the floating action button, navigation panel position, force expanded groups of preferences screens, remember screen, exit confirmation….

## Developers Guide and Demo Apk

* Inside the folder demo_and_guide you will find both resources. You can download and use without messing your settings system the demo app. You should install this app in priv-app if you want to use the Secure, Global and System section in the navigation panel. So, do not select that screen if you are not going to install the demo app in prv-app.

## License

* All the software developed by me in GrxSettings is free software, licensed uder Mozilla Pulbic License V 2.0. 
  see <https://www.mozilla.org/en-US/MPL/2.0//> 
	
* Related to the libraries used in this application, you will respect their license. Inside the source code you will find the corresponding license to be observed.


## Credits and Libraries

* [Floating Action Buton, by makovkastar (MIT License)](https://github.com/makovkastar/FloatingActionButton)

* [SublimeNavigationView, by vikramkakkar ((Apache License 2.0)](https://github.com/vikramkakkar/SublimeNavigationView)

* [QuadFlask ColorPicker, by QuadFlask ((Apache License 2.0))](https://github.com/QuadFlask/colorpicker)

* [Square ColorPicker by Sergey Margaritov (Apache License 2.0)](https://github.com/attenzione/android-ColorPickerPreference)

* [RootTools from kernelAdiutor (GPL 3.0 License)](https://github.com/Grarak/KernelAdiutor/tree/master/app/src/main/java/com/grarak/kerneladiutor/utils/root)

* [RootTools from kernelAdiutor (GPL 3.0 License)](https://github.com/Grarak/KernelAdiutor/tree/master/app/src/main/java/com/grarak/kerneladiutor/utils/root)

If I have forgotten to someone it was not on purpose, please tell me.

* Special thanks to Thomas Shimko & Therassad, from XDA for their help to add RTL support. 

* Many thanks also to sac23, from XDA,  for trusting in this application from the beginning

* And finally very very very very special thanks to my friends from EspDroids.com, the amazing Morogoku, gvm79, josete_1976, NeoVendetta, NecrosauroN,.... 
