

function LoginCtrl($rootScope, $cookieStore, $scope, $location, User, Logout, Email, facebookConnect) { 

    $scope.fbuser = {}
    $scope.error = null;
    $rootScope.loginoption = '';
    
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
                                           $location.url('welcome'); 
                                          }, 
                               function() {$scope.loginfail=true;}  );
                               
  }
  
  $scope.emailIt = function(email) {
    Email.send({type:'passwordrecovery', to:email, from:'info@littlebluebird.com', subject:'Password Recovery', message:'Your password is...'}, function() {alert("Your password has been sent to: "+email);}, function() {alert("Email not found: "+email+"\n\nContact us at info@littlebluebird.com for help");});
  }
  
}