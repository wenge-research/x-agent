<template>
    <div class="forbidden-container">
        <div class="error-content">
            <div class="error-icon">
                <img src="/src/assets/images/pic.png" alt="">
            </div>
            <div class="error-message">
                应用正在维护中，暂时无法访问
            </div>
            <span class="error-info">如有疑问请联系管理员</span>
        </div>
    </div>
</template>

<script setup>

</script>

<style lang="scss" scoped>
.forbidden-container {
    width: 100%;
    height: 100%;
    background: url('/src/assets/images/bg.png') no-repeat center;
    background-size: 100% 100%;
    display: flex;
    // align-items: center;
    justify-content: center;
    text-align: center;
    .error-icon{
        margin-top: 236px;
        margin-bottom: 26px;
    }
    .error-message{
        font-weight: 500;
        font-size: 18px;
        color: #3F4247;
        margin-bottom: 10px;
    }
    .error-info{
        font-weight: 400;
        font-size: 14px;
        color: #797F8A;
    }
}




</style>