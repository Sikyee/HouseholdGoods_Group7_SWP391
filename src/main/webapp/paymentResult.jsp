<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">

    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Kết quả giao dịch</title>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css"
              integrity="sha512-z3gLpd7yknf1YoNbCzqRKc4qyor8gaKU1qmn+CShxbuBusANI9QpRohGBreCFkKxLhei6S9CQXFEbbKuqLg0DA=="
              crossorigin="anonymous" referrerpolicy="no-referrer" />
        <style>
            .btn-back {
                text-decoration: none;
                padding: 10px 20px;
                background-color: red;
                color: white;
                border-radius: 10px;
                display: inline-block;
                margin-top: 20px;
            }
        </style>
    </head>

    <body style="
          background-color: #f4f4f4;
          font-family: Arial, sans-serif;
          margin: 0;
          padding: 0;
          height: 100vh;
          display: flex;
          justify-content: center;
          align-items: center;
          ">

        <section style="
                 text-align: center;
                 display: flex;
                 flex-direction: column;
                 justify-content: center;
                 align-items: center;
                 ">

            <div>
                <img src="https://th.bing.com/th/id/R.5d1d23fc5f58bcb60174a0cad9fae4f1?rik=a16ULpY8dAibig&pid=ImgRaw&r=0"
                     alt="Transaction Status"
                     style="width: 120px; height: 120px; margin-bottom: 20px;">
            </div>

            <%
                Boolean transResult = (Boolean) request.getAttribute("transResult");
                if (transResult != null && transResult) {
            %>
            <!-- Giao dịch thành công -->
            <div>
                <h3 style="font-weight: bold; color: #28a745;">
                    Your order paid successfully!
                    <i class="fas fa-check-circle"></i>
                </h3>
                <p style="font-size: 18px; margin-top: 15px;">Thank you for using our service.</p>
                <a href="/HouseholdGoods_Group7_SWP391/" class="btn-back">Back to home</a>
            </div>
            <%
            } else if (transResult != null && !transResult) {
            %>
            <!-- Giao dịch thất bại -->
            <div>
                <h3 style="font-weight: bold; color: #dc3545;">
                    Your order has been canceled!
                </h3>
                <a href="/HouseholdGoods_Group7_SWP391/" class="btn-back">Back to home</a>
            </div>
            <%
            } else {
            %>
            <!-- Đang xử lý giao dịch -->
            <div>
                <h3 style="font-weight: bold; color: #ffc107;">
                    We have received your order, please wait for processing!
                </h3>
                <a href="/HouseholdGoods_Group7_SWP391/" class="btn-back">Back to home</a>
            </div>
            <%
                }
            %>
        </section>

    </body>
</html>
