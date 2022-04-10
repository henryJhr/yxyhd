(function ($) {
    $.fn.drag = function (options, sucfun, errfun) {
        var startTime;
        var $gt_cut = $("#yzm_image_source");
        var $gt_cut_hidden = $("#yzm_image_cut_big");
        var $xy_img = $("#xy_img");
        //一、定义一个获取DOM元素的方法
        box = document.querySelector(".drag"),//容器
            bg = document.querySelector(".dragBg"),//背景
            text = document.querySelector(".dragText"),//文字
            btn = document.querySelector(".dragBtn"),//滑块
            button=document.querySelector("#button"),
            success = false, //是否通过验证的标志
            distance = box.offsetWidth - btn.offsetWidth;//滑动成功的宽度（距离）
        var offsetX;
        var downX;
        var handler = $(".dragBtn");
        var handler_parent = $("#drag_parent");
        var isMove = false;
        var isvalid = false;


        //二、给滑块注册鼠标按下事件
        handler.mousedown(function (e) {
            //1.鼠标按下之前必须清除掉后面设置的过渡属性
            btn.style.transition = "";
            bg.style.transition = "";

            //说明：clientX 事件属性会返回当事件被触发时，鼠标指针向对于浏览器页面(或客户区)的水平坐标。

            //2.当滑块位于初始位置时，得到鼠标按下时的水平位置
            var e = e || window.event;
            downX = e.clientX;
            $gt_cut.css("display", "none");
            $gt_cut_hidden.show();
            isMove = true;

        });


        //三、给文档注册鼠标移动事件
        document.onmousemove = function (e) {
            if (isMove) {
                text.innerHTML = "";
                startTime = new Date().getTime();
                var e = e || window.event;
                //1.获取鼠标移动后的水平位置
                var moveX = e.clientX;

                //2.得到鼠标水平位置的偏移量（鼠标移动时的位置 - 鼠标按下时的位置）
                offsetX = moveX - downX;
                $(".yzm").show();
                var animated_opacity = document.getElementsByClassName("animated_opacity")[0];
                animated_opacity.style.opacity = 1;
                animated_opacity.style.display = 'block';
                // display();


                console.log(offsetX);

                //3.在这里判断一下：鼠标水平移动的距离 与 滑动成功的距离 之间的关系
                if (offsetX > distance) {

                    // slideHintInfo.style.height = "0px";


                    offsetX = distance;//如果滑过了终点，就将它停留在终点位置
                } else if (offsetX < 0) {
                    offsetX = 0;//如果滑到了起点的左侧，就将它重置为起点位置
                }
                $xy_img.show();
                $xy_img.css({'left': offsetX > 220? 220 : offsetX});
                //4.根据鼠标移动的距离来动态设置滑块的偏移量和背景颜色的宽度
                btn.style.left = offsetX + "px";
                bg.style.width = offsetX + "px";
            }
        };


        // handler_parent.mouseleave(function (e) {
        //     console.log("移出范围");
        //     var animated_opacity = document.getElementsByClassName("animated_opacity")[0];
        //     $(".yzm").hide();
        //     animated_opacity.style.opacity = 0;
        //
        //
        // })


        //四、给文档注册鼠标松开事件
        document.onmouseup = function (e) {
            $(".yzm").addClass("animated_opacity");
            console.log("鼠标松开事件");
            isMove = false;

            if (offsetX > distance) {
                offsetX = distance;//如果滑过了终点，就将它停留在终点位置
            } else if (offsetX < 0) {
                offsetX = 0;//如果滑到了起点的左侧，就将它重置为起点位置
            }

            $.ajax({
                type: "POST",
                url: "captcha/checkCaptcha",
                dataType: "JSON",
                async: false,
                data: {point: offsetX},
                success: function (result) {
                    console.log(result);
                    if (result.code == 200) {
                        dragOk();
                    } else {
                        console.log("错误");
                        dragErr();
                    }

                }
            });

            //如果鼠标松开时，滑到了终点，则验证通过
            if (success) {
                console.log(success);
                return;
            } else {
                console.log(success);
                //反之，则将滑块复位（设置了1s的属性过渡效果）
                btn.style.left = 0;
                bg.style.width = 0;
                btn.style.transition = "left 1s ease";
                bg.style.transition = "width 1s ease";
            }

            //只要鼠标松开了，说明此时不需要拖动滑块了，那么就清除鼠标移动和松开事件。
            $(document).unbind('mousemove');
            $(document).unbind('mouseup');
            $(document).unbind('mousedown');

        }

        var errcount = 0;

        function dragErr() {
            text.innerHTML = "验证错误";
            text.style.color = "#e41181";
            bg.style.backgroundColor = "#bdfcd3";
            bg.style.width = "315px";
            btn.style.left = distance + "px";

            errcount = errcount + 1;

            /*if (errcount >= 3) {
                errfun();
                errcount = 0;
                $xy_img.css("display", "none");
                $gt_cut.show();
                $gt_cut.css("display", "none");
                $gt_cut_hidden.css("display", "none");
                // text.innerHTML="拖动滑块将悬浮图像正确拼合";
                // text.style.color="#999999";
            }*/

            setTimeout(function () {
                var animated_opacity = document.getElementsByClassName("animated_opacity")[0];
                animated_opacity.style.opacity = 0;
            }, 500);
            setTimeout(function () {
                var animated_opacity = document.getElementsByClassName("animated_opacity")[0];
                animated_opacity.style.display = 'none';
            }, 500);

            isvalid = false;
        }

        function dragOk() {
            isvalid = true;
            success = true;
            $(document).unbind('mousemove');
            $(document).unbind('mouseup');
            $(document).unbind('mousedown');

            //显示原始图片,并做光线闪过
            $xy_img.css("display", "none");
            $gt_cut_hidden.css("display", "none");
            $gt_cut.show();
            $gt_cut.addClass("run");

            //1.设置滑动成功后的样式
            text.innerHTML = "验证通过";
            text.style.color = "#39AA00";
            btn.innerHTML = "<img src='../themes/new/images/TIM20190320174041.png'>";
            btn.style.color = "#bdfcd3";
            bg.style.backgroundColor = "#bdfcd3";
            bg.style.width = "315px";
            btn.style.left = distance + "px";

            //2.设置滑动成功后的状态
            success = true;
            //成功后，清除掉鼠标按下事件和移动事件（因为移动时并不会涉及到鼠标松开事件）
            handler.unbind("mousedown");
            handler.unbind("mousemove")

            setTimeout(function () {
                var animated_opacity = document.getElementsByClassName("animated_opacity")[0];
                animated_opacity.style.opacity = 0;
            }, 500);
            setTimeout(function () {
                var animated_opacity = document.getElementsByClassName("animated_opacity")[0];
                animated_opacity.style.display = 'none';
            }, 500);
            //只要鼠标松开了，说明此时不需要拖动滑块了，那么就清除鼠标移动和松开事件。

            button.style.backgroundColor="#2d8cf0";
        }

        $('#button').click(function () {
            if (isvalid) {
                $('#password').val(rsaEncode($('#password').val()));
                $("form[name='casForm']").submit();
            }
        });


    };
})(jQuery);
