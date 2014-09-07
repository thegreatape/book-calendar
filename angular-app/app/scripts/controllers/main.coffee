'use strict'

###*
 # @ngdoc function
 # @name angularAppApp.controller:MainCtrl
 # @description
 # # MainCtrl
 # Controller of the angularAppApp
###
angular.module('angularAppApp')
  .controller 'MainCtrl', ($scope) ->
    $scope.awesomeThings = [
      'HTML5 Boilerplate'
      'AngularJS'
      'Karma'
    ]
