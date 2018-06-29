var app = angular.module('tinyReddit', []).controller('Controller', ['$scope', '$window',
  function($scope, $window) {

      var auth2;
      var googleUser;

      $scope.user = {};
      $scope.messages = {};
      $scope.lastMessages = {};
      $scope.myMessages = {};
      $scope.myVotes = {};

      var refreshList = function() {
        listHotMessages();
        listLastMessages();
        listMyMessages();
        listMyVotesMessages();
      };

      var listHotMessages = function() {
        gapi.client.messageendpoint.getAllMessages().execute(
          function(resp) {
            $scope.messages = resp.items;
            $scope.$apply();
            console.log(resp);
          });
      };

      var listLastMessages = function() {
        gapi.client.messageendpoint.getLastMessages().execute(
          function(resp) {
            $scope.lastMessages = resp.items;
            $scope.$apply();
            console.log(resp);
          });
      };

      var listMyMessages = function() {
        gapi.client.messageendpoint.getMyMessages({
          userID: $scope.user.id
        }).execute(
          function(resp) {
            $scope.myMessages = resp.items;
            $scope.$apply();
            console.log(resp);
          });
      };

      var listMyVotesMessages = function() {
        gapi.client.messageendpoint.getMyVotesMessages({
          userID: $scope.user.id
        }).execute(
          function(resp) {
            $scope.myVotes = resp.items;
            $scope.$apply();
            console.log(resp);
          });
      };

      $scope.addMessage = function() {
        gapi.client.messageendpoint.insertMessage({
          userID: $scope.user.id,
          fullName: $scope.user.fullName,
          title: $scope.title,
          body: $scope.body,
          imgUrl: $scope.imgUrl
        }).execute(
          function(resp) {
            console.log(resp);
            refreshList();
          });
      }

      // little hack to be sure that apis.google.com/js/client.js is loaded
      // before calling
      // onload -> init() -> window.init() -> then here
      $window.init = function() {
        console.log("windowinit called");
        var rootApi = 'https://1-dot-reddit-205206.appspot.com/_ah/api/';
        gapi.client.load('messageendpoint', 'v1', function() {
          console.log("message api loaded");
          refreshList();
        }, rootApi);

        gapi.load('auth2', initSigninV2);

      }

      var initSigninV2 = function() {
        console.log('initSigninV2()');
        auth2 = gapi.auth2.getAuthInstance();
        auth2.isSignedIn.listen(signinChanged);
        if(auth2.isSignedIn.get() == true) {
            auth2.signIn();
        }
      };

      var signinChanged = function(isSignedIn) {
          if(isSignedIn) {
              console.log('the user must be signed in to print this');
              var googleUser = auth2.currentUser.get();
              var authResponse = googleUser.getAuthResponse();
              var profile = googleUser.getBasicProfile();
              console.log('ID: ' + profile.getId());
              console.log('Full Name: ' + profile.getName());
              console.log('Given Name: ' + profile.getGivenName());
              console.log('Family Name: ' + profile.getFamilyName());
              console.log('Image URL: ' + profile.getImageUrl());
              console.log('Email: ' + profile.getEmail());
              $scope.user.id          = profile.getId();
              $scope.user.fullName    = profile.getName();
              $scope.user.firstName   = profile.getGivenName();
              $scope.user.lastName    = profile.getFamilyName();
              $scope.user.photo       = profile.getImageUrl();
              $scope.user.email       = profile.getEmail();
              $scope.$digest();
          } else {
              console.log('the user must not be signed in if this is printing');
              $scope.user = {};
              $scope.$digest();
          }
      };

      $scope.signOut = function() {
        console.log('signOut()');
        auth2.signOut().then(function() {
            signinChanged(false);
        });
      };

      $scope.vote = function(name, kindVote) {
        console.log('like msg');
        gapi.client.messageendpoint.likeMessage({
          name: name,
          userID: $scope.user.id,
          kindVote: kindVote
        }).execute(
          function(resp) {
            console.log(resp);
            refreshList();
          });
      };
  }
]);
