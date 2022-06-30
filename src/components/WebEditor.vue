<template>
    <div class="tinymce-editor">
        <editor
            v-model="myValue"
            :init="init"
            :disabled="disabled"
            @onClick="onClick"
        >
        </editor>
    </div>
</template>

<script>

import tinymce from "tinymce/tinymce";
import Editor from "@tinymce/tinymce-vue";
import "tinymce/themes/silver";
import 'tinymce/models/dom'
import "tinymce/icons/default";         //图标
import "tinymce/plugins/image";         // 插入上传图片插件
import "tinymce/plugins/table";         // 插入表格插件
import "tinymce/plugins/lists";         // 列表插件
import "tinymce/plugins/wordcount";     // 字数统计插件

import "tinymce/plugins/code";          //源代码
import "tinymce/plugins/preview";       //预览
import "tinymce/plugins/link";
import http from "@/http/http";

export default {
    name: "WebEditor",
    components: {
        Editor,
    },
    props: {
        value: {
            type: String,
            default: "",
        },

        baseUrl: {
            type: String,
            default: "",
        },
        disabled: {
            type: Boolean,
            default: false,
        },
        height: {
            type: [Number, String],
            required: false,
            default: 360,
        },
        plugins: {
            type: [String, Array],
            default: "lists image table wordcount code preview link ",
        },
        toolbar: {
            type: [String, Array],
            default() {
                return [
                    "undo redo | bold italic forecolor backcolor | lineheight | alignleft aligncenter alignright alignjustify | bullist numlist outdent indent ",
                    "formatselect | fontselect | fontsizeselect | lists link image table | removeformat | preview | code",
                ];
            },
        },
    },

    data() {
        return {
            init: {
                language_url: "/tinymce/langs/zh-Hans.js",
                language: "zh-Hans",
                skin_url: "/tinymce/skins/ui/oxide",
                content_css: "/tinymce/skins/content/default/content.css",
                // lineheight_formats:
                //   "8pt 9pt 10pt 11pt 12pt 14pt 16pt 18pt 20pt 22pt 24pt 26pt 36pt",
                fontsize_formats: "12px 14px 16px 18px 24px 36px 48px 56px 72px",
                font_formats:
                    "微软雅黑='微软雅黑';宋体='宋体';黑体='黑体';仿宋='仿宋';楷体='楷体';隶书='隶书';幼圆='幼圆';Andale Mono=andale mono,times;Arial=arial,helvetica,sans-serif;Arial Black=arial black,avant garde;Book Antiqua=book antiqua,palatino;Comic Sans MS=comic sans ms,sans-serif;Courier New=courier new,courier;Georgia=georgia,palatino;Helvetica=helvetica;Impact=impact,chicago;Symbol=symbol;Tahoma=tahoma,arial,helvetica,sans-serif;Terminal=terminal,monaco;Times New Roman=times new roman,times;Trebuchet MS=trebuchet ms,geneva;Verdana=verdana,geneva;Webdings=webdings;Wingdings=wingdings",
                // skin_url: '${this.baseUrl}/tinymce/skins/ui/oxide-dark', // 暗色系
                // content_css: '${this.baseUrl}/tinymce/skins/content/dark/content.css', // 暗色系
                height: 300,
                plugins: this.plugins,
                toolbar: this.toolbar,
                link_title: false,
                image_description: false,
                branding: false,
                menubar: false,
                // 此处为图片上传处理函数
                images_upload_handler: (blobInfo) => new Promise((resolve, reject) => {
                    if (blobInfo.blob().size / 1024 / 1024 > 2) {
                        reject("上传失败，图片大小请控制在 2M 以内")
                        return
                    }

                    http.upload("/upload", blobInfo.blob())
                        .then(res => {
                            const url = "http://124.71.194.255:8081/image/" + res.data.content.file
                            resolve(url)
                        })
                        .catch(err => {
                            reject(err.message)
                        })
                }),
            },
            myValue: this.value,
        };
    },
    mounted() {
        tinymce.init({ })
    },
    methods: {

        onClick(e) {
            this.$emit("onClick", e, tinymce)
        },

        clear() {
            this.myValue = ""
        },
    },
    watch: {
        value(newValue) {
            this.myValue = newValue
        },
        myValue(newValue) {
            this.$emit("input", newValue)
        },
    },
};
</script>
<style scoped>
</style>
