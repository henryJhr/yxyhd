$(function () {
  //登录事件
  $(document).keyup(function (event) {
    if (event.keyCode == 13) {
      login();
    }
  });
  $("#submit").click(function () {
    login();
  })
})

function login() {
  if ($(".account-input").val() == "") {
    $(".m-form-error-show").show();
    $(".m-form-error-show .m-form-error span").html("用户名不能为空");
  } else if ($(".input-eye-show").val() == "" && $(".input-eye-hide").val() == "") {
    $(".m-form-error-show").show();
    $(".m-form-error-show .m-form-error span").html("密码不能为空");
  } else {
    $(".m-form-error-show").hide();
    $.ajax({
      type: "POST",
      async: true,
      url: window.g.localhostUrl + "/api/login",
      dataType: 'json',
      data: {
        userName: $(".account-input").val(),
        password: $(".input-eye-show").val() || $(".input-eye-hide").val()
      },
      success: function (data) {
        if (data.code == '1') {
          window.location.href = window.g.localhostUrl + '/#/commodityIndex';
        } else {
          $(".m-form-error-show").show();
          $(".m-form-error-show .m-form-error span").html(data.msg);
        }
      },
      error: function (data) {
        console.log(data);
      }
    });
  }
}