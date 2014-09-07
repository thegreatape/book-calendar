'use strict'

###*
 # @ngdoc service
 # @name angularAppApp.calendar
 # @description
 # # calendar
 # Service in the angularAppApp.
###
angular.module('angularAppApp')
  .service 'Calendar', ->
    calendar = {}

    calendar.monthlyByAuthor = (books) ->
      authors = {}
      for book in books
        for author in book.authors
          authors[author.id] ||= {
            author: author
            months: {}
          }

          month = moment(book.publication_date).format("MMMM YYYY")
          authors[author.id]['months'][month] ||= []
          authors[author.id]['months'][month].push(book)

      _.values(authors)

    calendar
