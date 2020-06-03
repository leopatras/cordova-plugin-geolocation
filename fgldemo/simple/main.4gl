MAIN
  DEFINE results STRING
  DEFINE allresults STRING
  MENU
    BEFORE MENU
      CALL DIALOG.setActionHidden("cordovacallback",1)
    COMMAND "GetLocation"
      CALL getLocation()
    ON ACTION cordovacallback ATTRIBUTE(DEFAULTVIEW=NO)
      CALL ui.Interface.frontCall("cordova","getAllCallbackData",
                             ["Geolocation-"],[results])
      LET allresults=allresults.append(results)
      ERROR "results:",results
    ON ACTION viewresults ATTRIBUTE(TEXT="Results")
      OPEN WINDOW results WITH FORM "results"
      DISPLAY allresults TO te
      MENU
        ON ACTION close
          EXIT MENU
      END MENU
      CLOSE WINDOW results
    COMMAND "Exit"
      EXIT MENU
  END MENU
END MAIN

FUNCTION getLocation()
  DEFINE result STRING
  CALL ui.Interface.frontCall("cordova","callWithoutWaiting",
                             ["Geolocation","getLocation"],[result])
  ERROR "result=",result
END FUNCTION

FUNCTION addWatch()
  DEFINE result STRING
  CALL ui.Interface.frontCall("cordova","callWithoutWaiting",

                             ["Geolocation","addWatch"],[result])
  ERROR "result=",result
  RETURN result
END FUNCTION

FUNCTION clearWatch()
  DEFINE result STRING
  CALL ui.Interface.frontCall("cordova","call",
                             ["Geolocation","clearWatch"],[result])
  ERROR "result=",result
END FUNCTION

FUNCTION stopLocation()
  DEFINE result STRING
  CALL ui.Interface.frontCall("cordova","call",
                             ["Geolocation","stopLocation"],[result])
  ERROR "result=",result
END FUNCTION
