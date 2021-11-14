## Tokopedia products scrapper

### Tech:
- Kotlin
- Gradle
- Jsoup

### Overview

This exercise aims to retrieve 100 products from Handphone category in Tokopedia and save it to CSV file.
The CSV file contains name, description, price, and imageLink properties [*1].

### Flow

First, I need to explore all the HTML/CSS element to get properties that I am interested in and 
define object class called "Products" to store each product object. After that, I use the element list to start crawling
the website.

I am using JSoup library to scrape HTML content and "org.apache.commons.csv" to write result to CSV file.
The crawler begin with get and save the list of URLs from HandPhone category for further process, you can see the code in "getListProductUrl" method.
Next process is to iterate all the URLs to the properties and save it to Products object, please take a look at "getProductAttr"
method.

The last step is write all the collected data into CSV file, as you can see at saveToCsv method,
it needs to provide Products object, and save that object to CSV.

### How to run

`> ./gradlew run`
