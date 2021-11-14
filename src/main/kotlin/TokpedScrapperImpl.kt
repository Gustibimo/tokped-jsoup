
import mu.KotlinLogging
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVPrinter
import org.jsoup.Jsoup
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption

class TokpedScrapperImpl: Scrapper {

    private val tokpedHPUrl: String = "https://www.tokopedia.com/p/handphone-tablet/handphone"
    private val topData: Int = 100

    private val logger = KotlinLogging.logger {}
    override fun scrape() {
        val csvPath = "products.csv"
        val writer = Files.newBufferedWriter(Paths.get(csvPath))
        val csvPrinter = CSVPrinter(writer, CSVFormat.DEFAULT
            .withHeader("Product Name", "Descriptions", "Price", "imageLink"))
        csvPrinter.flush()

        getListProductUrl(tokpedHPUrl)
            .parallelStream()
            .map {
            getProductAttr(it)
        }
            .forEach {
            saveToCsv(it).flush()
//            logger.info("saving $it to csv")
            println("saving to csv for product:  $it")
        }
        csvPrinter.close()

    }

    private fun getListProductUrl(url: String): MutableList<String> {
        val urlList = mutableListOf<String>()
        val cssQuery = ".css-1dq1dix a"

        try {
            for (pageNumber in 1..20){
                println(pageNumber)
                val  res = Jsoup
                    .connect("$url?page=$pageNumber")
                    .timeout(50000)
                    .get()
                res.select(cssQuery)
                    .map { col -> col.attr("href") }
                    .parallelStream()
                    .forEach {
                        urlList.add(it)
                    } }
        } catch (e: IOException) {
            logger.error("Network error")
            throw NetworkErrorException("Error when getting data from url", e)
        }

        println(urlList.size)
        return urlList.subList(0,topData)

    }

    private fun getProductAttr(url: String): Products {

        try {
            val  res = Jsoup.connect(url)
                .timeout(50000)
                .get()

            val productName = res.select(".css-1wtrxts").text().replace(",", " ")
            val productDetails = res.select(".css-168ydy0").text().replace(",", " ")
            val price = res.select(".css-aqsd8m .price").text().substring(2)
            val merchant = res.select("#pdp_comp-shop_credibility")
            val imageLink = res.select(".css-1b60o1a img").attr("src")
            val ratingAttr = res.getElementsByAttributeValue("data-testid", "lblPDPDetailProductRatingNumber")


            val products = Products()
            products.name = productName
            products.description = productDetails
            products.price = price
            products.imageLink = imageLink

            return products
        } catch (e: IOException) {
            logger.error("Network error")
            throw NetworkErrorException("Error when getting data from url", e)
        }

    }

    private fun saveToCsv(products: Products): CSVPrinter {
        val csvPath = "products.csv"
        val writer = Files.newBufferedWriter(Paths.get(csvPath), StandardOpenOption.APPEND,
            StandardOpenOption.CREATE)
        val csvPrinter = CSVPrinter(writer, CSVFormat.DEFAULT)
        csvPrinter.printRecord(products.name, products.description, products.price, products.imageLink)
        return csvPrinter
    }
}