package com.pharbers.aqll.stub

import com.google.gson.Gson
import com.pharbers.aqll.alcalc.alemchat.sendMessage

/**
  * Created by Alfred on 09/03/2017.
  */
object stub_test_1 extends App {
	//{
	//// test case 1 : reading excel file and storage and portion
	//val s = alStorage(config/new_test/2016-01.xlsx""", new alIntegrateddataparser)
	//val ps = s.portion { lst =>
	//lst.grouped(10).map(iter => alPortion(iter)).toList
	//}
	//println(ps.isCalc)
	//println(ps.portions.head)
	//}

	//{
	//// test case 2 : reading memory and map func
	//val s = alStorage(List(1, 2, 3, 4, 5, 6, 7, 8, 9))
	//val s1 = s.map { x =>
	//x.asInstanceOf[Int] + 1
	//}
	//
	//val ps = s1.portion { lst =>
	//lst.grouped(2).map(iter => alPortion(iter)).toList
	//}
	//println(ps.portions.head)
	//}

	//    {
	//        // test case 3 : persistence test
	//        val s = alStorage("""config/new_test/2016-01.xlsx""", new alIntegrateddataparser)
	//        val ps = s.portion { lst =>
	//            lst.grouped(10).map(iter => alPortion(iter)).toList
	//        }
	//        alTextSync("""config/sync""", s)
	//        alTextSync("""config/sync/po""", ps)
	//        println(ps.portions.head)
	//    }

	//{
	//// test case 4 : restore data form text
	//val s = alStorage("""config/sync/po/0ae964e41b9bda2ff21045533bbcdda9""", new alTextParser)
	//println(s.isCalc)
	//s.doCalc
	//println(s.data)
	//}

	//{
	//// test case 5 : compress data ** gzip
	//val p = new pkgCmd("""config/group/ad0046ee-4669-4ed3-a781-cea9995c97f4""" :: Nil, """config/compress/test2/aa""")
	//p.excute
	//}
	//
	//{
	// test case 6 : restore from compress file ** gzip
	//val lst = FileOpt("/Users/qianpeng/Desktop/Test").lstFiles
	//lst foreach { x =>
	//new unPkgCmd(s"${x.substring(0, x.lastIndexOf("tar")-1)}", "/Users/qianpeng/Desktop/Test2").excute
	//}
	//val path = "/Users/qianpeng/Desktop/Test2"
	//if(!FileOpt(path).isDir) FileOpt(path).createDir

	//val p = new unPkgCmd("""config/compress/test""", """config/compress""")
	//p.excute
	//}

	//    {
	//        // test case 7 : scp ** gzip // 需要提前部署scp rsa秘钥
	//        val p = new scpCmd("""config/compress/test.tar.gz""", """""", "59.110.31.215", "root")
	//        p.excute
	//    }

	//{
	//// test case 8 : cp ** gzip
	//val p = new cpCmd("""config/compress/test.tar.gz""", """~/Desktop/test""")
	//p.excute
	//}

	//{
	//
	//cur = Some(new pkgCmd("""config/group/ad0046ee-4669-4ed3-a781-cea9995c97f4""" :: Nil, """config/compress/test2/aa""") :: Nil)
	//process = do_pkg() :: Nil
	//super.excute()
	////a.cc("你好")
	////a.cc("你好2")
	//}

	{
//			FileOpt("""/Users/qianpeng/Desktop/scp/098f6bcd4621d373cade4e832627b4f6.py""").rmFiles
//		val a = FileOpt("""/Users/qianpeng/Desktop/scp2""").rmAllFiles
//		println(a)

		//val a1 = "aaa你好"
		//val a2 = "aaaaaa你好"
		//val b1 = "bbb你好啊"
		//println(a1.hashCode)
		//println(a2.hashCode)
		//println(b1.hashCode)


		//val local_price=new BigDecimal(0.015)
		//val exchange_rate = new BigDecimal(2)
		////0.02999999999999999888977697537484345957636833190917968750
		//println(local_price.multiply(exchange_rate))
		//
		////0.030
		//import scala.math.BigDecimal
		////0.0319998
		//val a = BigDecimal(0.0159999092321049857939849) /  BigDecimal(2.900998764537)
		//println(a.getClass)
		//println(a)
		//println(a.toDouble)
		//println(0.0159999092321049857939849 / 2.900998764537)
		//
		//println(BigDecimal("0.0") *  BigDecimal("0.751879699"))

		//t(new alCalcParmary("BMS"))
		//println(ListBuffer((2015, "BMS"), (2015, "BMS"), (2016, "BMS")) distinct)


//		val tmp = "0c1b2e69-e33d-4cce-961e-acfea075151f" ::
//			"0f7a08a3-fabd-491f-bedd-b1f48c716b98" ::
//			"10ef5c56-5924-4189-b5ca-8e53ca1c0989" ::
//			"16eb7340-5e97-49ad-af38-92b0da3e8ff1" ::
//			"17cc178e-9158-4fc3-96bb-f73b20cd38c8" ::
//			"1c5bf2b0-03ce-4fb8-89ee-b246cffa1e0b" ::
//			"292ed74a-95fe-4004-aaff-b886bd5245cc" ::
//			"293b6330-c6dd-426f-9968-9b348940c099" ::
//			"2c6651d4-b593-4243-8052-d202cf789453" ::
//			"2d8527b8-5cad-4ae9-be81-477f95b04626" ::
//			"315d26e6-f9b0-4ad9-84fa-b217d6bd7175" ::
//			"32d6a6fa-25f6-4a40-91c2-15269e884118" ::
//			"3cf41fcb-9ba1-4e9f-b68c-cec50bce0241" ::
//			"3de188d8-796c-4db6-bdca-7a66a6e391c3" ::
//			"3e6a7f7b-e9e9-4901-b940-451ace5542a2" ::
//			"3fcb2a1e-5031-4615-b925-153152eb4951" ::
//			"4054cdb6-02a5-4b9f-81f5-885c192c9035" ::
//			"40c33622-a698-45f4-b33e-66d03973c87b" ::
//			"42c89ef0-ae79-46b3-be02-684347211fdd" ::
//			"4becd757-6e42-4d84-8ada-114de55fb9b6" ::
//			"55187609-4272-4644-ae3d-a52c1e2bb052" ::
//			"5f8b5c74-4e80-447e-9a6c-8f3e4e38bc98" ::
//			"60eb0010-c3b9-45c9-b78e-912e842335f7" ::
//			"675d35e7-1895-41cb-88be-d20794123987" ::
//			"68f40b95-1d63-4a5a-9278-a1d1722d9d0a" ::
//			"6bf825ac-978d-490b-8b66-4894b78a1a10" ::
//			"6d2e915b-226d-4d7f-8e36-3a5a3322183f" ::
//			"6db05311-" ::
//			"6df7730c-" ::
//			"6f6ba04c-6b73-4078-8d72-932f2da723c9" ::
//			"746a6b8a-55d8-4bd0-a3e4-f6231714d810" ::
//			"7567c544-fbc6-4024-89b9-db01ec9019f8" ::
//			"789cef8c-d1a1-48e8-bf3f-850cc1f08a12" ::
//			"79bbdd08-1b39-45b5-a0f9-ac86c8695b7f" ::
//			"79dd5f1c-5350-4d49-b518-80f91c83d17d" ::
//			"7e110194-1b8d-4c9c-afeb-d91570cfc318" ::
//			"7fdc3766-494c-4114-bd67-2779b7b0aac9" ::
//			"81b3027d-61b5-45c9-892b-cdfed0ca23a0" ::
//			"8456a6d2-708c-4b52-98a8-3b092c9ccfe0" ::
//			"84894110-cb09-44d2-b39e-6672d881b71d" ::
//			"858df863-1594-457c-83f2-5703eae2b165" ::
//			"91660001-01e6-4614-bcc9-4d29349172b4" ::
//			"923f18c0-d04e-44e3-9a50-bc695d3dc92e" ::
//			"98f77981-b666-429c-afb3-7e3a4ec4ac6e" ::
//			"9bcbcaf8-6d0f-494a-afe4-071ffe8344df" ::
//			"9d65c2de-186c-4905-a4d4-afecc36a3aeb" ::
//			"9e2e295f-4476-4ca1-b931-1eedc6e0e973" ::
//			"9f9e68f0-5a12-41c1-96a5-2dd38b961b75" ::
//			"Test" ::
//			"a957d837-288d-4c22-98f4-d97d96c17fed" ::
//			"aadae57a-568b-4126-9866-f0fdcaf445d9" ::
//			"ac33a304-d7c4-45e0-930d-d059f3a48ade" ::
//			"ae340220-7e1f-4084-8c58-8aa1ed3a7954" ::
//			"af46052a-33f6-4f7c-b992-92f5f1d49603" ::
//			"be54ad8e-481b-4ff9-9bc8-0b88807591b9" ::
//			"bedc416d-c4b8-40cc-8030-cc9e2b3446c2" ::
//			"c49bde9d-4866-48cd-a85c-5abf80b72193" ::
//			"c9b39327-76ff-4429-8857-392822de0c7f" ::
//			"cdb47b24-b89d-49b0-b3c8-b32025fbd600" ::
//			"d143339e-4969-4bc0-88ae-2ad5e7f4724b" ::
//			"d398485f-ca2b-4e60-b599-8fa9226dc4df" ::
//			"d43ebc28-81f6-45b8-8a3d-66fd2c4de044" ::
//			"d544bad9-775a-4817-89a5-c0b51758bfde" ::
//			"d6702874-2c9a-4d60-8baf-7b0b438062ae" ::
//			"d6b03805-" ::
//			"d71f2142-0ab6-438e-80b3-264365e94111" ::
//			"e1888190-280e-40d8-9ec6-1162e6777f05" ::
//			"e3f35836-11c2-4cc1-89d4-c96fbfb623f3" ::
//			"e75b3a57-07d0-4d8b-8bb4-dc443de979c6" ::
//			"e7c4eb1e-a2da-47d0-b6c2-0c276f104d19" ::
//			"f29d8cfd-1b53-45ba-a873-ec6850573482" ::
//			"f485a963-d84c-4247-b81b-a2e0f92dc113" ::
//			"test" :: Nil
//
//		val tmp2 = "06faf622-b1b1-4fdb-bb68-de2e303a29d2" ::
//			"08618c83-0ac8-4013-98f1-7a6202891297" ::
//			"127f5f21-c50f-42dc-8519-dda75ecae514" ::
//			"1b141fdd-fd64-4e85-9a15-e9ba7c880481" ::
//			"1c82cb88-4011-4a55-9b05-e06d275ec0f1" ::
//			"1db5bf8d-c409-42f7-8191-991356159a95" ::
//			"212658ae-9a3c-4756-a338-19f03fbb5982" ::
//			"2222d308-fd87-4625-89ff-026999792f58" ::
//			"2466eb04-531a-4223-a2ac-e1ed7a6984ef" ::
//			"24e59fb0-baf4-49ba-9b96-fa47b04b05e8" ::
//			"24fc9820-33c4-48a7-b2c1-a8e9db8e57a1" ::
//			"2733fc34-de27-4c5d-b379-c2660ad4389d" ::
//			"2cba4a7c-6ddc-4207-9cc2-42f839602986" ::
//			"2cec9095-3b37-42ab-b551-7830aa6c8fba" ::
//			"2d00f4dc-c1e7-4804-b4a3-14a0b1315c49" ::
//			"366837f4-2a36-472e-9b26-27b35aa2542f" ::
//			"384d69b5-598d-4339-9655-2c9b6920b719" ::
//			"3c5bf026-881f-47bc-ac16-d972dda063a1" ::
//			"3c971e69-" ::
//			"40ee602c-bd40-4b88-be33-4ae314135f7b" ::
//			"41f9045a-31fc-4186-b44e-8e099e89c009" ::
//			"434592f7-387c-4d0a-b2fd-7ec0eaa37dc2" ::
//			"4554a64f-73dc-4538-a4c9-21a141e39206" ::
//			"45a74875-1331-4f09-8921-c616fe8f19ce" ::
//			"49c15e83-" ::
//			"5253d12a-b930-4048-902c-164e09809c13" ::
//			"61a29331-3885-4950-a028-d73cd5e1dad1" ::
//			"638ed441-8fb1-43ab-8864-3d2580c7c3a3" ::
//			"6610d417-25a7-4df0-ac09-7a83bd0e86f1" ::
//			"6738a191-8eb1-407b-b891-5563ae002576" ::
//			"684b79cb-551c-47ab-8457-1d169f5c4213" ::
//			"6a20e54f-0401-4832-872a-4f6d4dd0dda2" ::
//			"6f9ca223-038d-48b6-9d21-2298683b968f" ::
//			"79cf7c43-0bcb-49c6-bd9c-93e6fce4c893" ::
//			"7de99e5b-3a97-4c8b-9088-aae04407c8a4" ::
//			"7e4414af-ac24-449d-8c72-f310023f211f" ::
//			"8067f71d-d435-404f-a9d2-6299584c34d0" ::
//			"80d7c2cf-ec4b-497c-87d2-6740f0071c3e" ::
//			"816830b8-86d0-40ac-8874-f7528243d4eb" ::
//			"89bcbd43-d0bf-4434-a9e5-818c7062b3ea" ::
//			"8cbd8420-45c4-4b15-927a-6b45f5e631f7" ::
//			"8e73f513-1db0-488d-a863-e263a485c503" ::
//			"8ebb43bc-b7b1-450b-9d90-45ea622fc98b" ::
//			"8ed1f04f-b57c-49e4-adfa-dbca70c4caf4" ::
//			"9194fc31-bc9f-439b-9c04-6b4b86fdd5f4" ::
//			"94ffb6d8-3bed-49fd-9778-59628d052016" ::
//			"96a3dde3-53e4-4363-8df7-9e826dd7239e" ::
//			"Test" ::
//			"a13d7c17-22a9-4778-9682-2a60ebc8a75d" ::
//			"a4a6f175-4de4-4c1b-a43e-a781f118fdb0" ::
//			"a7456520-964c-4d5e-8e06-920bda030b82" ::
//			"a7b86835-95e1-4c72-8d7c-044c2ad4d321" ::
//			"adfb5f83-1dd7-4573-bd4b-18923505c5d8" ::
//			"b18d792d-f4f6-468e-9fbc-17c870b09f04" ::
//			"b4de8ea6-df32-4a45-b869-4db8a49aa63f" ::
//			"b607bf49-f01d-4c76-8ec3-1bfd7913ee56" ::
//			"bd39d8df-bef2-4309-bdc6-452a0dc70b7e" ::
//			"c5709199-330c-446a-b9a2-15e75035d762" ::
//			"c9354627-" ::
//			"ca3fc829-ef9e-4242-afc5-727c7cb58d66" ::
//			"cac3ffd7-cafd-4f62-b3b3-718506672571" ::
//			"cbdd5adf-e929-47a8-bd3d-dceb03266959" ::
//			"cc98e12a-0b0b-4f39-b975-bb2e14dd94a6" ::
//			"d191ec3e-77d7-4ba6-a52e-224dda476df9" ::
//			"d44593fc-4416-495b-a2f5-2e569462b8d2" ::
//			"d81501d9-e05a-4251-943f-89e6bec2d37c" ::
//			"e15525b8-9eaa-4846-861c-a1af15bd0584" ::
//			"e2c32b43-9889-4c23-a869-1a43fe3eaef6" ::
//			"e894c34b-eaea-490d-829a-417942e4e024" ::
//			"e981c4aa-b0c6-4f6d-abe2-4664bb19ba54" ::
//			"f7adda0e-" ::
//			"f9014f64-6984-4f99-9d91-63e931f40703" ::
//			"f99b22df-bc46-483f-8ddb-f1504c3ab827" ::
//			"fa48a947-44e3-414d-8eb4-a828e9fb4245" ::
//			"ff8c6bd6-dd14-4f59-8e03-2172852f0d38" ::
//			"test" :: Nil
//
//		tmp foreach { x =>
//			_data_connection.getCollection(x).drop()
//		}
//
//		tmp2 foreach { x =>
//			_data_connection.getCollection(x).drop()
//		}
//		println(MD5.md5("Pain"))

//		_data_connection_thread.getCollection("test").drop()
	}

	//def t(c: alCalcParmary): Unit = {
	//println(c.year)
	//c.year = 2017
	//println(c.year)
	//println(c.market)
	//println(c.company)
	//println(c.uuid)
	//}

//	FileOpt("""/Users/qianpeng/Desktop/scp""").rmAllFiles

	implicit def Int2Number(t: java.lang.Integer): Int = t.intValue
}

//object a {
//def cc(s:String) = {
//new bb(s).t()
//}
//}
//
//class bb(s:String) {
//def t() ={
//println(s)
//}
//}