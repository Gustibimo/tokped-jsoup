class Products {
    var name :String = ""
    var description :String = ""
    var imageLink :String = ""
    var price :String = ""
    var rating: String = ""
    var merchant :String = ""

    override fun toString(): String {
        return "Products(name=$name, description=$description, price=$price, imageLink=$imageLink)"
    }
}