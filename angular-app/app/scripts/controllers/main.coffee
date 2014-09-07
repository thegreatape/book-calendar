'use strict'

###*
 # @ngdoc function
 # @name angularAppApp.controller:BooksCtrl
 # @description
 # # BooksCtrl
 # Controller of the angularAppApp
###
angular.module('angularAppApp')
  .controller 'BooksCtrl', ($scope) ->
    $scope.booksByAuthor = -> []
