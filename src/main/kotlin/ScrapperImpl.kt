
import mu.KotlinLogging
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVPrinter
import org.jsoup.Jsoup
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption

class ScrapperImpl: Scrapper {

    private val tokpedHPUrl = "https://www.tokopedia.com/p/handphone-tablet/handphone"

    private val logger = KotlinLogging.logger {}
    override fun scrape() {
        val csvPath = "products.csv"
        val writer = Files.newBufferedWriter(Paths.get(csvPath))
        val csvPrinter = CSVPrinter(writer, CSVFormat.DEFAULT
            .withHeader("Product Name", "Descriptions", "Price", "imageLink"))
//        try {
//            val doc = Jsoup
//                .connect("https://www.tokopedia.com/coppersurabaya/copper-cp1008-charger-single-port-up-to-20watt-qc-3-0-fast-charging")
//                .get()
//            println(doc.title())
//        } catch (e: IOException){
//            logger.error("Error when get the url {}", e.message)
//        }

        getListProductUrl(tokpedHPUrl).parallelStream().map {
            getProductAttr(it)
        }.forEach {
            saveToCsv(it).flush()
            println(it)
        }

    }

    private fun getListProductUrl(url: String): MutableList<String> {
        val urlList = mutableListOf<String>()
        val cssQuery = ".css-1dq1dix a"

        for (pageNumber in 1..3){
            println(pageNumber)
            val  res = Jsoup.connect("$url?page=$pageNumber").get()
            res.select(cssQuery)
                .map { col -> col.attr("href") }
                .parallelStream()
                .forEach {
                    urlList.add(it)
                }
        }

        return urlList.subList(0,3)

    }

    private fun getProductAttr(url: String): Products {

        val  res = Jsoup.connect(url).get()

        val productName = res.select(".css-1wtrxts").text().replace(",", " ")
        val productDetails = res.select(".css-168ydy0").text().replace(",", " ")
        val price = res.select(".css-aqsd8m .price").text().substring(2)
        val merchant = res.select(".css-1vh65tm div")
        val imageLink = res.select(".css-1b60o1a img").attr("src")
        val ratingAttr = "lblPDPDetailProductRatingNumber"

        val products = Products()
        products.name = productName
        products.description = productDetails
        products.price = price
        products.imageLink = imageLink


        return products
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