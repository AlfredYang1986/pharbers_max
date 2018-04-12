package com.pharbers.aqll.module.fopModule

import java.io._
import play.api.libs.Files
import play.api.libs.json.Json._
import play.api.libs.json.JsValue
import play.api.mvc.MultipartFormData
import play.api.libs.Files.TemporaryFile

import com.pharbers.ErrorCode._
import com.pharbers.sercuity.Sercurity.{md5Hash => md5}
import com.pharbers.common.another_file_package.fileConfig._
import com.pharbers.common.datatype.string.PhStringOpt.removeSpace

/**
  * Created by liwei on 2017/4/7.
  */
object SliceUpload {

    // TODO ：不知道为啥报错，回头解决@我自己 钱鹏
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
                        val company = data.dataParts("company").head
                        val date = data.dataParts("date").head
                        val market = data.dataParts("market").head

                        md5(company + date + removeSpace(market))
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
    
    //测试
    def ManyFileSlice2(data: MultipartFormData[Files.TemporaryFile]): JsValue = {
        try {
            data.files.foreach { x =>
                val t_lst = getPathByFileType(data)
                println(t_lst)
                val filename = t_lst.head match {
                    case "Hospital" => {
                        val company = data.dataParts("company").head
                        val date = data.dataParts("date").head
                        val market = data.dataParts("market").head
                        md5(company + date + removeSpace(market))
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
        val filetype = data.dataParts("filetype").head
        val company = data.dataParts("company").head
        val outpath = filetype match {
//            case "CPA" => s"$root$program$fileBase$company$client_cpa_file"
//            case "GYCX" => s"$root$program$fileBase$company$client_gycx_file"
//            case "Manager" => s"$root$program$fileBase$company$manage_file"
//            case "Hospital" => s"$root$program$fileBase$company$hospitalData"
            case "CPA" => s"$fileBase$company$client_cpa_file"
            case "GYCX" => s"$fileBase$company$client_gycx_file"
            case "Manager" => s"$fileBase$company$manage_file"
            case "Hospital" => s"$fileBase$company$hospitalData"
        }
        val dirfile = new File(outpath)
        if (!dirfile.exists()) {
            dirfile.mkdirs()
        }
        filetype :: s"${dirfile.getPath}/" :: Nil
    }
}
