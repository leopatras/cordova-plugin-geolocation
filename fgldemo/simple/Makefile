BUNDLE_IDENTIFIER   = com.fourjs.geolocation
APPNAME             = GeoG
DEVICE              = phone
MODE                = debug
VERBOSE             = no
IDENTITY='(59W7RY75ZU)'
PROVISIONING_PROFILE=/Users/leo/Library/MobileDevice/Provisioning\ Profiles/4js_wildcard
FORMS   = $(patsubst %.per, %.42f, $(wildcard *.per))

#GMA settings.. GMA needs an absolute path to the plugin
#so we use some gnu make features to build it
PLUGIN=../..
ABSPATH=$(abspath .)
DEMODIR=$(shell  dirname $(ABSPATH))
PLUGINDIR=$(shell dirname $(DEMODIR))
GMAPLUGIN=$(notdir $(PLUGINDIR))
#GMI
GMIPLUGIN=cordova-plugin-geolocation
# in case you build the demo when having the plugin checked out of github 
# then we can use a relative build path for GMI
ifneq ($(wildcard ../../fgldemo),)
  GMIPLUGIN=$(CURDIR)/../..
#  FGLLDPATH=../../fgl
endif

MODULES  = $(patsubst %.4gl, %.42m, $(wildcard *.4gl))
FORMS    = $(patsubst %.per, %.42f, $(wildcard *.per))

%.42m: %.4gl
	fglcomp -M --resolve-calls $*

%.42f: %.per
	fglform -M $*

all:: $(MODULES) $(FORMS)

define gmibuild
	VERBOSE=$(VERBOSE) gmibuildtool --install=$(1) --verbose=$(VERBOSE) --app-name=$(APPNAME) --bundle-id=$(BUNDLE_IDENTIFIER) --provisioning=$(PROVISIONING_PROFILE) --certificate=$(IDENTITY) --device=$(DEVICE) --mode=$(MODE) --build-cordova=$(GMIPLUGIN)
endef

gmi.build: all
	$(call gmibuild,no)

gmi.install: all
	$(call gmibuild,yes)

gma.build: all 
#build the native plugin out of this repository  and install it in GMA
	cd ../../.. && gmabuildtool scaffold --install-plugins $(GMAPLUGIN) && cd -
	gmabuildtool build --build-quietly --build-cordova $(GMAPLUGIN) --build-app-package-name $(BUNDLE_IDENTIFIER) 

gma.install: gma.build
	$$ANDROID_SDK_ROOT/platform-tools/adb install -r ./gma/project/app/build/app/outputs/apk/app-unsigned.apk


clean::
	rm -f *.sch *.dbs *.42*
	rm -rf build bin distbin gma
	rm -f cordovamap.txt gmi/*.a gmi/link_flags.sh *.4pwvs *.4pwdb ../../src/ios/*.a
	rm -f ../../pluginCalls.*

