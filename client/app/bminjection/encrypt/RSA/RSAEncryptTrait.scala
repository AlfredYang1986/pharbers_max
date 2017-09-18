package bminjection.encrypt.RSA

import bminjection.encrypt.EncryptTrait
import bmutil.dao.{_data_connection, from}
import com.mongodb.casbah.Imports._

/**
  * Created by alfredyang on 01/06/2017.
  */
trait RSAEncryptTrait extends javaEncryptTrait with EncryptTrait {
      lazy val queryKeys : (String, String) = {
        (from db() in "encrypt_config" where ("project" -> "PIC") select (x => x)).toList match {
            case head :: Nil => {
                (head.getAs[String]("public_key").get, head.getAs[String]("private_key").get)
            }
            case Nil => {
                val keyMap = RSAUtils.genKeyPair
                val pub_key = RSAUtils.getPublicKey(keyMap)
                val pri_key = RSAUtils.getPrivateKey(keyMap)

                val builder = MongoDBObject.newBuilder
                builder += "project" -> "PIC"
                builder += "public_key" -> pub_key
                builder += "private_key" -> pri_key

                _data_connection.getCollection("encrypt_config") += builder.result

                (pub_key, pri_key)
            }
        }
    }

    override lazy val publicKey : String = queryKeys._1
    override lazy val privateKey : String = queryKeys._2
}
