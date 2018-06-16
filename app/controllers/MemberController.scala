package controllers

import javax.inject._
import play.api.mvc._
import play.api.db.Database




/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class MemberController @Inject()(db: Database, cc: ControllerComponents) extends AbstractController(cc) {

  import models._

  /**
    * Create an Action to render an HTML page.
    *
    * The configuration in the `routes` file means that this method
    * will be called when the application receives a `GET` request with
    * a path of `/`.
    */
  def index() = Action { implicit request: Request[AnyContent] =>
    val members = Member.getAllMembers()
    Ok(views.html.members.index.render(members))
  }

  def register() = Action { implicit request: Request[AnyContent] =>
    request.session.get("connected").map { user =>
      println("Register " + user)
    }.getOrElse {
      Unauthorized("Oops, you are not connected")

    }
    Ok(views.html.members.register())
  }

  def save() = Action { implicit request: Request[AnyContent] =>
    val conn = db.getConnection()
    try{
      val statement = conn.createStatement
      val sql = "INSERT INTO MEMBER (id,password) VALUES ('"+request.body.asFormUrlEncoded.get("user_id")(0)+"','"+request.body.asFormUrlEncoded.get("user_pw")(0)+"')"
      println(sql)
      statement.executeUpdate(sql)
    }finally{
      conn.close()
    }
    Ok(views.html.members.login("회원가입을 축하합니다."))
  }

  def login() = Action { implicit request: Request[AnyContent] =>

    if(request.session.get("connected").size==1){
      val userid:String = request.session.get("connected").toList(0)
      Ok(views.html.members.successPage(userid))
    }else{
      Ok(views.html.members.login(""))
    }

  }

  def logout() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.members.login("로그아웃 되었습니다.")).withNewSession
  }

  def checkPassword() = Action { implicit request: Request[AnyContent] =>

    val conn = db.getConnection()
    try{
      val statement = conn.createStatement
      val user_id = request.body.asFormUrlEncoded.get("id")(0)
      val password = request.body.asFormUrlEncoded.get("password")(0)
      val sql = s"SELECT id,password,name FROM MEMBER WHERE id ='${user_id}' AND password='${password}'"

      val rs = statement.executeQuery(sql)
      println(sql)

      if(rs.next()){
        Ok(views.html.members.successPage(user_id)).withSession(
          "connected" -> user_id)
      }else {
        Ok(views.html.members.login("아이디나 비밀번호가 맞지 않습니다."))
      }
    }finally{
      conn.close()
    }
  }


  def changeInfoPage() = Action { implicit request: Request[AnyContent] =>
    val conn = db.getConnection()
    try{
      val statement = conn.createStatement
      val user_id = request.body.asFormUrlEncoded.get("id")(0)
      val sql = s"SELECT id,password,name FROM MEMBER WHERE id ='${user_id}'"

      val rs = statement.executeQuery(sql)
      println(sql)

      var member_info:Member=null
      if(rs.next()) {
        member_info = new Member(
          rs.getString("name"),
          rs.getString("name"),
          rs.getString("id"),
          rs.getString("password")
        )
      }
      Ok(views.html.members.change(member_info))
    }finally{
      conn.close()
    }
  }

  def changeInfo() = Action { implicit request: Request[AnyContent] =>
    val conn = db.getConnection()

    try{
      val statement = conn.createStatement
      val id = request.body.asFormUrlEncoded.get("user_id")(0)
      val password = request.body.asFormUrlEncoded.get("user_pwd")(0)
      val sql = s"UPDATE MEMBER SET password ='${password}' WHERE id = '${id}'"

      statement.executeUpdate(sql)
      Ok(views.html.members.successPage(id))
    }finally{
      conn.close()
    }


  }

  def deleteMember() = Action{implicit request:Request[AnyContent] =>
    val user_id = request.body.asFormUrlEncoded.get("id")(0)
    Member.deleteMember(user_id)
    val conn = db.getConnection()
    try{
      val statement = conn.createStatement
      val sql = "DELETE FROM MEMBER WHERE id = '"+request.body.asFormUrlEncoded.get("id")(0)+"'"
      println(sql)
      statement.executeUpdate(sql)
    }finally{
      conn.close()
    }

    Ok(views.html.members.login("탈퇴완료 되었습니다."))
  }

}



