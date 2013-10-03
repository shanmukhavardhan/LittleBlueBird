// 2013-09-23 just stuck this here - being lazy

var source = new EventSource('/serversentevents');
  
// 2013-09-23 just stuck this here - being lazy
function sseCtrl($scope) {
 
  $scope.items = [];
 
  source.addEventListener('right', function(e) {
    $scope.$apply(function() {
      $scope.items.push(e.data);
    });
  },false);
}


function LoginCtrl($rootScope, $cookieStore, $scope, $location, User, Logout, Email, facebookConnect, $window, $timeout) { 

    $scope.fbuser = {}
    $scope.error = null;
    $rootScope.loginoption = '';
    
  $scope.showModal = function(tf) {
    $rootScope.xxxModalShown = tf;
    console.log('$rootScope.xxxModalShown = ', $rootScope.xxxModalShown);
  }
  
  $scope.mobileanswer = function(yn) {
    if(yn == 'yes') {
  
      // LBB IS NOT -> https://itunes.apple.com/us/app/nfl-pro-2014-ultimate-football/id518244464?mt=8&uo=4
      var appStoreURL = "https://itunes.apple.com/us/app/nfl-pro-2014-ultimate-football/id518244464?mt=8&uo=4";
      
      // http://stackoverflow.com/questions/13044805/how-to-check-if-an-app-is-installed-from-a-web-page-on-an-iphone
      // Replace the link below with the link to LittleBlueBird in the app store
      // Use this if you have to figure out what the LBB link is: https://linkmaker.itunes.apple.com/us/
      setTimeout(function () { window.location = appStoreURL; }, 25);
      window.location = "littlebluebird://";
    }
    else if(yn == 'no') {}
  }
    
  $scope.loginhelpbox = function(showhide) {
    console.log("scope.loginhelpbox, using rootScope -----------------------------");
    $rootScope.loginhelp = showhide;
  }
  
  $scope.setloginsectiontwo = function(somename) { $rootScope.loginsectiontwo = somename; }
  
 
  $scope.login = function() {
    //alert("login:  "+$scope.username+" / "+$scope.password);
    if(!angular.isDefined($scope.username) || !angular.isDefined($scope.password)) {
      $scope.loginfail=true;
      
      console.log("scope.login:  didn't want this to happen");
      return;
    }
    
      console.log("scope.login:  made it this far at least");
      
    $rootScope.user = User.find({username:$scope.username, password:$scope.password}, 
                               function() {$scope.loginfail=false; 
                                           if($rootScope.user.dateOfBirth == 0) { $rootScope.user.dateOfBirth = ''; }
                                           $rootScope.showUser = $rootScope.user;  
                                           console.log("scope.login:  set 'user' cookie");
                                           $cookieStore.put("user", $rootScope.user.id);
                                           $cookieStore.put("showUser", $rootScope.showUser.id);
                                           console.log("scope.login:  go to 'welcome'");
                                           
                                           // Now see if there was a 'proceedTo' url that the user was trying to go to before being redirected to the login page...
                                           // See app-FacebookModule.js.  We also check for this variable there.
										   if(angular.isDefined($rootScope.proceedTo)) {
										       $location.url($rootScope.proceedTo);
										       delete $rootScope.proceedTo;
										   }
                                           else
                                               $location.url('welcome'); 
                                          }, 
                               function() {$scope.loginfail=true;}  );
                               
  }
  
  // 2013-07-19 duplicated in the mobile version ForgotCtrl
  $scope.emailIt = function(email) {
    Email.send({type:'passwordrecovery', to:email, from:'info@littlebluebird.com', subject:'Password Recovery', message:'Your password is...'}, function() {alert("Your password has been sent to: "+email);}, function() {alert("Email not found: "+email+"\n\nContact us at info@littlebluebird.com for help");});
  }
  
}