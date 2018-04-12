import com.pharbers.sercuity.Sercurity
import org.scalatestplus.play._

class UtilSpec extends PlaySpec with OneAppPerTest {
    "MD5" should {
        "test md5 for string" in {
            val result = Sercurity.md5Hash("恩华药业集团有限责任公司")
            println(s"result = $result")
            result != "" mustBe true
        }
    }
}