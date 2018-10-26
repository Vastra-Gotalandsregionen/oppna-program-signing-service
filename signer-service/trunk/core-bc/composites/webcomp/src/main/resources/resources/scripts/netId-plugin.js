$(document).ready(function() {
  var $tbs = $('input[name="DataToBeSigned"]')
  var $postUrl = $('input[name="PostURL"]')
  var typeOfPlugin = isSignIntalled()

  if (typeOfPlugin != false) {
    // Ladda in plugins
    initSignController(typeOfPlugin, $tbs.val(), $postUrl.val())

    // Starta signeringen
    retVal = document.iid.Invoke('Sign');

    if(retVal !== 0) { // User Abort
      $('#errorCode').attr('value', retVal)
      $("#cancel-form").submit() 
    } else {
      // Success
      postSignature($tbs.val(), $postUrl.val());
    }

  } else {
    $('#errorCode').attr('value', 2)
    $("#cancel-form").submit() 
  }
});

//Kontrollerar om NetId är installerad genom att försöka så undviker man
//beroende till webbläsarnamn o.s.v.
var isSignIntalled = function() {
  try {
    var myObj = new ActiveXObject("IID.iIDCtl");
    if (myObj) {
      return "ActiveX";
    }
  } catch (e) {
    if(navigator.plugins) {
      if (navigator.plugins.length > 0) {
        if (navigator.mimeTypes && navigator.mimeTypes["application/x-iid"]) {
          if (navigator.mimeTypes["application/x-iid"].enabledPlugin) {
            return "Plugin";
          }
        }
      }
    }
    return false;
  }
}


var initSignController = function(typePlugin, tbs, postUrl) {
  if( typePlugin == "Plugin" ) {
    $('body').append("<object name='iid' width='0', height='0', type='application/x-iid'></object>")
  } else {
    $('body').append('<object name="iid" width=0 height=0 classid="CLSID:5BF56AD2-E297-416E-BC49-00B327C4426E"></object>');
  }
  document.iid.SetProperty('DataToBeSigned', tbs);
  document.iid.SetProperty('IncludeCaCert', 'true');
  document.iid.SetProperty('IncludeRootCaCert', 'true');
  document.iid.SetProperty('Base64', 'true');
  document.iid.SetProperty('SignReturnName', 'signature');
  document.iid.SetProperty('DataReturnName', 'encodedTbs');

}

var postSignature = function (tbs, postUrl) {
  var payload = {
    encodedTbs: tbs,
    signature: decodeURIComponent(document.iid.GetProperty('signature'))
  };
  var form = document.createElement('form');
  form.style.visibility = 'hidden';
  form.method = 'POST';
  form.action = postUrl;
  var keys = Object.keys(payload);
  for (index in keys) {
    var key = keys[index];
    var input = document.createElement('input');
    input.name = key;
    input.value = payload[key];
    form.appendChild(input);
  }
  document.body.appendChild(form);
  form.submit();
};