const {defineConfig} = require('@vue/cli-service')
module.exports = defineConfig({
    transpileDependencies: true,
    devServer: {
        proxy: {
            '/api': {
                target: 'http://124.71.194.255:8081',
                changeOrigin: true,
                ws: true,
                pathRewrite: {
                    '^/api': '',
                }
            }
        }
    }
})

