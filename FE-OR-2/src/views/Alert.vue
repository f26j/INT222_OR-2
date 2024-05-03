<template>
  <div class="container">
    <!-- section-success -->
    <div class="wrapper-success">
      <div class="card">
        <div class="icon"><i class="fas fa-check-circle"></i></div>
        <div class="subject">
          <h3>Success</h3>
          <p>Anyone with access can view your invited visitors</p>
        </div>
        <div class="icon-times"><i class="fas fa-times"></i></div>
      </div>
    </div>
    <!-- section-success -->

    <!-- section-info -->
    <div class="wrapper-info">
      <div class="card">
        <div class="icon"><i class="fas fa-info-circle"></i></div>
        <div class="subject">
          <h3>Info</h3>
          <p>Anyone with access can view your invited visitors</p>
        </div>
        <div class="icon-times"><i class="fas fa-times"></i></div>
      </div>
    </div>
    <!-- section-info -->

    <!-- section-warning -->
    <div class="wrapper-warning">
      <div class="card">
        <div class="icon"><i class="fas fa-exclamation-circle"></i></div>
        <div class="subject">
          <h3>Warning</h3>
          <p>Anyone with access can view your invited visitors</p>
        </div>
        <div class="icon-times"><i class="fas fa-times"></i></div>
      </div>
    </div>
    <!-- section-warning -->
  </div>
</template>

<style>
    * {
        margin: 0;
        padding: 0;
        box-sizing: border-box;
      }
      body {
        background-color: #f6f6f8;
        font-family: Poppins;
      }
      /* success */
      .wrapper-success {
        margin: 120px;
      }
      .wrapper-success .card {
        width: 720px;
        height: 120px;
        background-color: #fff;
        padding: 10px 20px;
        display: flex;
        justify-content: space-around;
        align-items: center;
        border-left: 5px solid #49d761;
        border-radius: 3px;
        box-shadow: rgba(149, 157, 165, 0.2) 0px 8px 24px;
      }
      .wrapper-success .card .subject {
        margin-right: 180px;
      }
      .wrapper-success .card .subject p {
        color: #909092;
      }
      .wrapper-success .card .icon {
        font-size: 28px;
        color: #49d761;
      }
      .wrapper-success .card .icon-times {
        font-size: 28px;
        color: #c3c2c7;
        cursor: pointer;
      }
      /* success */
      /* info */
      .wrapper-info {
        margin: 120px;
      }
      .wrapper-info .card {
        width: 720px;
        height: 120px;
        background-color: #fff;
        padding: 10px 20px;
        display: flex;
        justify-content: space-around;
        align-items: center;
        border-left: 5px solid #3286e9;
        border-radius: 3px;
        box-shadow: rgba(149, 157, 165, 0.2) 0px 8px 24px;
      }
      .wrapper-info .card .subject {
        margin-right: 180px;
      }
      .wrapper-info .card .subject p {
        color: #909092;
      }
      .wrapper-info .card .icon {
        font-size: 28px;
        color: #3286e9;
      }
      .wrapper-info .card .icon-times {
        font-size: 28px;
        color: #c3c2c7;
        cursor: pointer;
      }
      /* info */
      /* warning */
      .wrapper-warning {
        margin: 120px;
      }
      .wrapper-warning .card {
        width: 720px;
        height: 120px;
        background-color: #fff;
        padding: 10px 20px;
        display: flex;
        justify-content: space-around;
        align-items: center;
        border-left: 5px solid #fdc220;
        border-radius: 3px;
        box-shadow: rgba(149, 157, 165, 0.2) 0px 8px 24px;
      }
      .wrapper-warning .card .subject {
        margin-right: 180px;
      }
      .wrapper-warning .card .subject p {
        color: #909092;
      }
      .wrapper-warning .card .icon {
        font-size: 28px;
        color: #fdc220;
      }
      .wrapper-warning .card .icon-times {
        font-size: 28px;
        color: #c3c2c7;
        cursor: pointer;
      }
      /* warning */
</style>