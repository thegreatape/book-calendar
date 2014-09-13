'use strict'

###*
 # @ngdoc overview
 # @name angularAppApp
 # @description
 # # angularAppApp
 #
 # Main module of the application.
###
angular
  .module('angularAppApp', [
    'ngAnimate',
    'ngCookies',
    'ngResource',
    'ngRoute',
    'ngSanitize',
    'ngTouch',
    'doowb.angular-pusher'
    'config'
  ])
  .config(($routeProvider) ->
    $routeProvider
      .when '/',
        templateUrl: 'views/books.html'
        controller: 'BooksCtrl'
      .otherwise
        redirectTo: '/'
  ).config((PusherServiceProvider, ENV) ->
    PusherServiceProvider.setToken(ENV.PUSHER_KEY).setOptions({})
  )
