package com.pharbers.aqll.module.fopModule

import play.api.libs.Files.TemporaryFile
import play.api.mvc.MultipartFormData
import java.io._
import com.pharbers.aqll.common.alFileHandler.fileConfig._
import play.api.libs.json.Json._
import play.api.libs.json.JsValue
import com.pharbers.aqll.common.alEncryption.alEncryptionOpt
import com.pharbers.aqll.common.alString.alStringOpt
import com.pharbers.aqll.common.alErrorCode.alErrorCode._

/**
  * Created by liwei on 2017/4/7.
  */
object SliceUpload {

    // TODO : 多文件上传后台代码
    // TODO : 多文件上传的核心是，前端的文件队列里面，文件一个一个排着队，等第一个文件上传完了，在上传第二个文件，
    // TODO : 前端反复多次调用这个方法，mulitiFIleFileName为当前正在上传的文件名
    def ManyFileSlice(data: MultipartFormData[TemporaryFile]): JsValue = {
        try {
            //var lst : List[JsValue] = Nil
            data.files.foreach { x =>
                val t_lst = getPathByFileType(data)
                val filename = t_lst.head match {
                    case "Hospital" => {
                        val company = data.dataParts.get("company").get.head
                        val date = data.dataParts.get("date").get.head
                        val market = data.dataParts.get("market").get.head
                        alEncryptionOpt.md5(company + date + alStringOpt.removeSpace(market))
                    }
                    case _ => x.filename
                }
                MergeSliceFile(s"${t_lst.tail.head}$filename", x.ref.file)
                //lst = lst :+ toJson(filename)
            }
            toJson(successToJson())
        } catch {
            case ex: Exception => errorToJson(ex.getMessage)
        }
    }

    // TODO : 实现原理，以读写的方式打开目标文件，将分片文件缓冲流输入
    def MergeSliceFile(outPath: String, tempFile: File) {
        var raFile: RandomAccessFile = null
        var inputStream: BufferedInputStream = null
        try {
            val dirFile = new File(outPath)
            dirFile.createNewFile()
            // TODO : 以读写的方式打开目标文件(rw)
            raFile = new RandomAccessFile(dirFile, "rw")
            raFile.seek(raFile.length)
            inputStream = new BufferedInputStream(new FileInputStream(tempFile))
            val buf = new Array[Byte](1024)
            var length = 0
            while ((length = inputStream.read(buf)) != -1) raFile.write(buf, 0, length)
        } catch {
            case ioex: IOException => throw new IOException(ioex.getMessage)
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close()
                }
                if (raFile != null) {
                    raFile.close()
                }
            } catch {
                case ex: Exception => throw new Exception(ex.getMessage)
            }
        }
    }

    // TODO : 根据前台传过来的文件类型归档文件
    def getPathByFileType(data: MultipartFormData[TemporaryFile]): List[String] = {
        val filetype = data.dataParts.get("filetype").get.head
        val company = data.dataParts.get("company").get.head
        val outpath = filetype match {
            case "CPA" => s"$root$program$fileBase$company$client_cpa_file"
            case "GYCX" => s"$root$program$fileBase$company$client_gycx_file"
            case "Manager" => s"$root$program$fileBase$company$manage_file"
            case "Hospital" => s"$root$program$fileBase$company$hospitalData"
        }
        val dirfile = new File(outpath)
        if (!dirfile.exists()) {
            dirfile.mkdirs()
        }
        filetype :: s"${dirfile.getPath}/" :: Nil
    }
}
