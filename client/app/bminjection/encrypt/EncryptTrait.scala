package bminjection.encrypt

import java.security.interfaces.{RSAPrivateKey, RSAPublicKey}

/**
  * Created by alfredyang on 01/06/2017.
  * 鉴于RSA的为非对称加密，在加密的过程中的安全性最高，所有优先使用
  * 如果以后RSA在解密效能上产生了重大问题，可以考虑是用DES加密token
  */
trait EncryptTrait {
    // RSA
    val publicKey : String
    val privateKey : String

    // DES
    val desKey : String = "what ever a key" // 随便一个什么key
}
