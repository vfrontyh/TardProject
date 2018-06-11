package models


import scala.collection._

case class Member(var first_nm: String, var last_nm:String, var id: String, var password: String)

object Member {
  private var members = mutable.HashMap[String,Member]()
  members+=("KTLEE"->new Member("KT","LEE","KTLEE","11"))
  members+=("YHNA"->new Member("YH","NA","YHNA","22"))
  members+=("DHKIM"->new Member("DH","KIM","DHKIM","33"))
  def addMember(first_nm: String, last_nm:String, id: String, password:String) = {
    val member = new Member(first_nm,last_nm,id, password)
    println(id)
    members+=(id -> member)
  }
  def getMember(id:String): Member ={
      members(id)
  }

  def getAllMembers() : mutable.HashMap[String,Member] = {
    members
  }

  def deleteMember(id:String): Unit ={
    members-=(id)
  }
}

