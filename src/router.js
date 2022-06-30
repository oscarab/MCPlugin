import {createRouter, createWebHashHistory} from "vue-router";
import WebHome from './pages/WebHome'
import LoginPage from './pages/LoginPage'
import RegisterPage from './pages/RegisterPage'
import IndexPage from './pages/IndexPage'
import PluginsPage from './pages/PluginsPage'
import PluginInfoPage from './pages/PluginInfoPage'
import EditPluginPage from './pages/EditPluginPage'
import NotFound from './pages/NotFound'
import DocumentPage from './pages/DocumentPage'
import EditDocumentPage from './pages/EditDocumentPage'
import UserPage from './pages/UserPage'
import EditCopyPage from './pages/EditCopyPage'
import CopyPage from './pages/CopyPage'
import SearchPage from './pages/SearchPage'

const routes = [
    {
        path: "/",
        component: WebHome,
        redirect: '/index',
        children: [
            {
                path: "index",
                component: IndexPage
            },
            {
                path: "login",
                component: LoginPage
            },
            {
                path: "register",
                component: RegisterPage
            },
            {
                path: "plugins",
                component: PluginsPage
            },
            {
                path: "plugin/:id",
                component: PluginInfoPage
            },
            {
                path: "plugin/:id/edit",
                component: EditPluginPage
            },
            {
                path: "document/:id",
                component: DocumentPage
            },
            {
                path: "document/:id/edit",
                component: EditDocumentPage
            },
            {
                path: "copy/:id",
                component: CopyPage
            },
            {
                path: "copy/:id/edit",
                component: EditCopyPage
            },
            {
                path: "user/:id",
                component: UserPage
            },
            {
                path: "/search",
                component: SearchPage
            },
            {
                path: ':pathMatch(.*)*',
                component: NotFound
            },
        ]
    }
]

const router = createRouter({
    history: createWebHashHistory(),
    routes
})

export default router
