## About AardvarkX

AardvarkX takes over an target's computer screen and plays music while mutating the screen in response to user actions. (Note that all mutation is done on a screenshot, and no other applications are actually affected.)

## Compatibility

The current version is targeted at Windows machines; it won't work very well on others because the methods used to fill the screen and prevent exit are very different. The program will run normally without errors on other systems, but some of the key features may not work as expected, especially on Macs. Versions targeted at different systems or a unified version that detects the host OS may be released in the future, but it's not a priority right now.

## Building a Quick-Install Disk (Windows)

Create a directory called AardvarkX and place it on the root of a disk (flash drives and CDs work well). Compile the program into a self-contained jarfile called `AardvarkX.jar` and place it in the directory. Place `aardvark.bat` and `start.vbs` in the same directory. Zip installandrun.bat; it can go anywhere.

You can also download our pre-packaged installation and unzip it into the root of some portable media.

## Usage

To install (on Windows) open (but do not decompress) the zip file Installer `inside.zip`. Open `installandrun.bat`, and click "Run" when prompted. Doing this circumvents restrictions on running programs from external media. The AardvarkX folder MUST be on the root of a drive for it to work. AardvarkX should start automatically when the installer finishes; when it does, the drive can be safely removed. The program now lives on the user's computer. (Please note that the program is copied to and run from a temporary folder, and is not actually installed in the usual sense of the word.)

AardvarkX has a few options you can set on startup. There are three activation modes. "On focus" will activate the program with a screenshot (so the target suspects nothing) as soon an you select another window. This way you can bring a program the target had open back to the front so the screenshot looks natural. "Immediately" starts it, well, duh. "Timer" will let you set a clock time or a countdown timer to activate the program. You will be given a window in which you can select a countdown of a certain amount of time, or a countdown to a certain time. You can also specify whether the program should start playing the music immediately at the activation time, or wait for input as with the other activation methods. The program will be invisible until it goes off.

A backdoor password will let you interrupt and exit AardvarkX. You can change the default password in the configuration dialog, as well as disable it. When the program is running, type a period or a forward slash followed by the password and AardvarkX will disappear. AardvarkX will continue to generate fake text and mouse motion as you type, but it will not show what you type, or give any indication that you are typing the correct code.

If the release at end option is checked, the program will exit with a short message after the music finishes. Otherwise, the music will loop and the computer will remain locked down until it is forcibly powered off or the backdoor password is entered.