package controllers
import javax.inject.Inject

import play.api.db._
import play.api.mvc._




/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class MemberController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

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
    Ok(views.html.members.register())
  }

  def save() = Action { implicit request: Request[AnyContent] =>
    Member.addMember(request.body.asFormUrlEncoded.get("first_nm")(0), request.body.asFormUrlEncoded.get("last_nm")(0), request.body.asFormUrlEncoded.get("user_id")(0), request.body.asFormUrlEncoded.get("user_pw")(0))
    Ok(views.html.members.login("회원가입을 축하합니다."))
  }

  def login() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.members.login(""))
  }

  def checkPassword() = Action { implicit request: Request[AnyContent] =>
    val user_id = request.body.asFormUrlEncoded.get("id")(0)
    val password = request.body.asFormUrlEncoded.get("password")(0)
    println(user_id + password)
    val exist = Member.getAllMembers().filter(_._1 == user_id).size
    if (exist > 0) {
      val testMember = Member.getMember(user_id)
      if (testMember.id == user_id && password == testMember.password) {
        Ok(views.html.members.successPage(user_id))
      } else {
        Ok(views.html.members.login("아이디나 비밀번호가 맞지 않습니다용."))
      }
    } else {
      Ok(views.html.members.login("아이디나 비밀번호가 맞지 않습니다."))
    }
  }

  def changeInfo() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.members.login(""))
  }

  def deleteMember() = Action{implicit request:Request[AnyContent] =>
    val user_id = request.body.asFormUrlEncoded.get("id")(0);
    Member.deleteMember(user_id);
    Ok(views.html.members.login("탈퇴완료 되었습니다."))
  }

}



