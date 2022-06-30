<template>

    <el-dialog v-model="showDialog" title="添加共同作者" @close="closeDialog">
        <el-row justify="center">
            <el-col :span="3">
                <span>用户名</span>
            </el-col>
            <el-col :span="10">
                <el-input
                    v-model="searchName"
                    placeholder="输入用户名..."
                />
            </el-col>
            <el-col :span="3">
                <el-button type="primary" @click="search">搜索</el-button>
            </el-col>
        </el-row>
        <el-row justify="center" style="margin-top: 2%">
            <el-select v-model="selectUser" placeholder="请选择用户" @change="allow=true">
                <el-option
                    v-for="item in searchResult"
                    :key="item.id"
                    :label="item.username"
                    :value="item.id"
                >
                    <el-avatar class="avatar"
                               style="float: left"
                               :src="item.avatar"
                               size="small">
                        <img src="https://opmanage.233leyuan.com/image/F81SJ7MxrpkPV1DKwIHIhBQ5H2MXFq7E.jpg" alt="">
                    </el-avatar>
                    <span style="float: right"> {{ item.username }} </span>
                </el-option>
            </el-select>
        </el-row>
        <el-row  justify="space-evenly" style="margin-top: 2%">
            <el-button type="primary"
                       :disabled="!allow"
                       @click="addAuthor(selectUser)">确定</el-button>
        </el-row>

    </el-dialog>

    <el-card class="big_card" v-loading="loading">
        <el-container v-if="!loading">
            <el-main>
                <el-form :model="plugin"
                         :rules="rules"
                         ref="ruleForm"
                         hide-required-asterisk="true"
                         size="medium"
                         label-width="20%"
                         class="plugin_form">
                    <el-form-item label="插件名称" prop="name">
                        <el-input v-model="plugin.name"/>
                    </el-form-item>
                    <el-form-item label="服务端类型" type="password" prop="serverType">
                        <el-select v-model="plugin.serverType" placeholder="请选择">
                            <el-option
                                v-for="item in type_option"
                                :key="item.value"
                                :label="item.label"
                                :value="item.value"
                            />
                        </el-select>
                    </el-form-item>
                    <el-form-item label="服务端版本" prop="serverVersion">
                        <el-input v-model="plugin.serverVersion"/>
                    </el-form-item>
                    <el-form-item label="插件类型" prop="pluginType">
                        <el-select v-model="plugin.pluginType" placeholder="请选择">
                            <el-option
                                v-for="item in plugin_type_option"
                                :key="item.value"
                                :label="item.label"
                                :value="item.value"
                            />
                        </el-select>
                    </el-form-item>
                    <el-form-item label="前置插件" prop="depends">
                        <el-input v-model="plugin.depends"/>
                    </el-form-item>
                    <el-form-item label="下载地址" prop="download">
                        <el-input v-model="plugin.download"/>
                    </el-form-item>
                </el-form>
                <el-divider/>
                <el-collapse style="margin-left: 5%; margin-right: 5%">
                    <el-collapse-item title="前言">
                        <WebEditor style="margin-left: 2%; margin-right: 2%"
                                    :value="plugin.preface"
                                    @input="plugin.preface = $event"/>
                    </el-collapse-item>
                    <el-collapse-item title="功能列表">
                        <WebEditor style="margin-left: 2%; margin-right: 2%"
                                   :value="plugin.functionList"
                                   @input="plugin.functionList = $event"/>
                    </el-collapse-item>
                    <el-collapse-item title="使用方法">
                        <WebEditor style="margin-left: 2%; margin-right: 2%"
                                   :value="plugin.usageMethod"
                                   @input="plugin.usageMethod = $event"/>
                    </el-collapse-item>
                    <el-collapse-item title="更新记录">
                        <WebEditor style="margin-left: 2%; margin-right: 2%"
                                   :value="plugin.updateRecord"
                                   @input="plugin.updateRecord = $event"/>
                    </el-collapse-item>
                </el-collapse>
                <el-divider/>
                <el-col style="margin-left: 5%; margin-right: 2%"> 插件作者 </el-col>
                <el-col>
                    <el-tag
                        v-for="tag in this.plugin.authors"
                        :key="tag.id"
                        style="font-size: medium;"
                        closable
                        :disable-transitions="false"
                        @close="removeAuthor(tag.id)"
                    >
                        {{ tag.username }}
                    </el-tag>
                </el-col>
                <el-col>
                    <el-button size="small" style="margin-left: 5%" @click="inputAuthor">
                        + 共同作者
                    </el-button>
                </el-col>
            </el-main>
            <el-footer>
                <el-button type="primary"
                           style="margin-top: 2%"
                           @click="submit">确认</el-button>
            </el-footer>
        </el-container>
        <el-skeleton v-else :rows="10" animated />
    </el-card>
</template>

<script>
import http from "@/http/http";
import storage, {popMessage, validateForm} from "@/http/api";
import WebEditor from "@/components/WebEditor";

export default {
    name: "EditPluginPage",

    data() {
      return {
          loading: true,
          plugin: {
              id: null,
              name: "",
              serverType: "",
              serverVersion: "",
              pluginType: "",
              depends: "",
              download: "",
              preface: "",
              functionList: "",
              usageMethod: "",
              updateRecord: "",
              authors: []
          },
          showDialog: false,
          searchName: "",
          searchResult: [],
          selectUser: "",
          allow: false,
          rules: {
              name: [{required: true, message: "请输入插件名字", trigger: "blur"}],
              serverType: [{required: true, message: "请输入服务端类型", trigger: "blur"}],
              serverVersion: [{required: true, message: "请输入服务端版本", trigger: "blur"}],
              pluginType : [{required: true, message: "请输入插件类型", trigger: "blur"}],
              download: [{required: true, message: "请输入下载地址", trigger: "blur"}],
          },
          type_option: [
              {
                  value: "0",
                  label: "CraftBukkit"
              },
              {
                  value: "1",
                  label: "Paper"
              },
              {
                  value: "2",
                  label: "Spigot"
              },
              {
                  value: "3",
                  label: "CatServer"
              },
              {
                  value: "4",
                  label: "Sponge"
              },
              {
                  value: "5",
                  label: "BungeeCord"
              },
              {
                  value: "6",
                  label: "其他"
              },
          ],
          plugin_type_option: [
              {
                  value: "0",
                  label: "娱乐"
              },
              {
                  value: "1",
                  label: "RPG"
              },
              {
                  value: "2",
                  label: "综合"
              },
              {
                  value: "3",
                  label: "信息"
              },
              {
                  value: "4",
                  label: "管理"
              },
              {
                  value: "5",
                  label: "经济"
              },
              {
                  value: "6",
                  label: "安全"
              },
              {
                  value: "7",
                  label: "API"
              },
              {
                  value: "8",
                  label: "其他"
              },
          ],
          type_map: { "CraftBukkit": "0", "Paper": "1", "Spigot": "2",
                       "CatServer": "3", "Sponge": "4", "BungeeCord": "5",
                       "Other": "6" },
          plugin_type_map: { "Fun": "0", "RPG": "1", "Comprehensive": "2",
              "Information": "3", "Manage": "4", "Economic": "5",
              "Security": "6", "API": "7", "Other" : "8" },
      }
    },

    mounted() {
        if (this.$route.params.id === "0") {
            this.loading = false
            this.plugin.authors.push(storage.load("user"))
            return
        }
        http.get("/plugins/" + this.$route.params.id + "/info", null ,
            res => {
                this.plugin.id = res.content.id
                this.plugin.name = res.content.name
                this.plugin.serverVersion = res.content.serverVersion
                this.plugin.serverType = this.type_map[res.content.serverType]
                this.plugin.pluginType = this.plugin_type_map[res.content.pluginType]
                this.plugin.depends = res.content.depends
                this.plugin.download = res.content.download
                this.plugin.preface = res.content.preface
                this.plugin.functionList = res.content.functionList
                this.plugin.usageMethod = res.content.usageMethod
                this.plugin.updateRecord = res.content.updateRecord
                this.plugin.authors = this.plugin.authors.concat(res.content.authors)
                this.loading = false
            },
            err => {
                popMessage("错误", err.message)
                this.$router.replace("/404")
            })
    },

    methods: {
        submit() {
            validateForm(this.$refs["ruleForm"]).then(() => {
                if (this.plugin.id === null) {
                    http.post("/plugins/create", this.plugin,
                        () => {
                            popMessage("提示", "成功新建插件")
                            this.$router.back()
                        },
                        err => {
                            popMessage(err.message)
                        })
                    return
                }
                http.post("/plugins/save", this.plugin,
                    () => {
                        popMessage("提示", "成功编辑插件")
                        this.$router.back()
                    },
                    err => {
                        popMessage("错误", err.message)
                    })
            })
        },
        closeDialog() {
            this.showDialog = false
            this.searchResult = []
            this.searchName = ""
            this.selectUser = ""
            this.allow = false
        },
        inputAuthor() {
            this.showDialog = true
        },
        addAuthor(selected) {
            const result = this.searchResult.find( user => user.id === selected )
            this.plugin.authors.push(result)
            this.closeDialog()
        },
        removeAuthor(id) {
            if (id === storage.load("user").id) {
                return
            }
            const result = this.plugin.authors.findIndex( user => user.id === id )
            this.plugin.authors.splice(result, 1)
        },
        search() {
            if (this.searchName.length === 0) return
            http.get("/user/search/" + this.searchName, null,
                res => {
                    this.searchResult = res.content
                    const id = storage.load("user").id
                    const result = this.searchResult.findIndex( user => user.id === id )
                    if (result !== -1)
                        this.searchResult.splice(result, 1)
                    this.plugin.authors.map(user => {
                        const result = this.searchResult.findIndex( u => u.id === user.id )
                        if (result !== -1)
                            this.searchResult.splice(result, 1)
                    })
                },
                err => {
                    popMessage("错误", err.message)
                })
        },
    },

    components: {
        WebEditor
    }
}
</script>

<style scoped>
.big_card {
    margin-left: 10%;
    margin-right: 10%;
    margin-top: 1%;
    height: 100%;
    border-radius: 10px;
}

.plugin_form {
    margin-left: 10%;
    margin-right: 20%;
}
</style>
