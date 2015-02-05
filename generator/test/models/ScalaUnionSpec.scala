package models

import generator.{ScalaCaseClasses, ScalaClientMethodConfigs, ScalaService}
import com.gilt.apidoc.generator.v0.models.InvocationForm
import org.scalatest.{ShouldMatchers, FunSpec}

class ScalaUnionSpec extends FunSpec with ShouldMatchers {

  val clientMethodConfig = ScalaClientMethodConfigs.Play23("test.apidoc")

  val json = TestHelper.buildJson("""
      "unions": [
        {
          "name": "user",
          "plural": "users",
          "types": [
            { "type": "registered_user" },
            { "type": "guest_user" }
          ]
        }
      ],

      "models": [
        {
          "name": "registered_user",
          "plural": "registered_users",
          "fields": [
            { "name": "id", "type": "long", "required": true },
            { "name": "email", "type": "string", "required": true },
            { "name": "name", "type": "string", "required": false },
            { "name": "foo", "type": "string", "required": true }
          ]
        },
        {
          "name": "guest_user",
          "plural": "guest_users",
          "fields": [
            { "name": "id", "type": "long", "required": true },
            { "name": "email", "type": "string", "required": true },
            { "name": "name", "type": "string", "required": false },
            { "name": "bar", "type": "string", "required": true }
          ]
        }
      ]
  """)

  lazy val service = TestHelper.service(json)
  lazy val ssd = ScalaService(service)

  it("generates valid models") {
    val code = ScalaCaseClasses.invoke(InvocationForm(service), addHeader = false)
    TestHelper.assertEqualsFile("test/resources/scala-union-case-classes.txt", code)
  }

  it("generates valid readers / writes for the union type itself") {
    val user = ssd.unions.find(_.name == "User").get
    val code = Play2Json(ssd).generateUnion(user)
    TestHelper.assertEqualsFile("test/resources/scala-union-json.txt", code)
  }

  it("models that are part of a union type only have readers") {
    val registeredUser = ssd.models.find(_.name == "RegisteredUser").get
    val code = Play2Json(ssd).generateModel(registeredUser)
    TestHelper.assertEqualsFile("test/resources/scala-union-model-json.txt", code)
  }

}