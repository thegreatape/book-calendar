'use strict'

###*
 # @ngdoc function
 # @name angularAppApp.controller:BooksCtrl
 # @description
 # # BooksCtrl
 # Controller of the angularAppApp
###
angular.module('angularAppApp')
  .controller 'BooksCtrl', ($scope, $http, Calendar, Pusher) ->

    $scope.start = moment("2014-01-01")
    $scope.end = moment("2015-06-01")

    months = for i in _.range(0, 18)
      moment($scope.start).add(i, 'month').format("MMMM YYYY")

    $scope.months = months

    $scope.books = []
    $scope.authors = []
    $scope.$watchCollection 'books', =>
      $scope.authors = Calendar.monthlyByAuthor($scope.books)

    Pusher.subscribe "user-#{2003928}", 'book', (book) ->
      $scope.books.push book
