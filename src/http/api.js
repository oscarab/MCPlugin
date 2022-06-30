import {ElNotification} from "element-plus";

export function validateForm(form) {
    return new Promise((resolve, reject) => {
        form.validate((valid) => {
            if (valid) {
                resolve(valid)
            } else {
                reject(valid)
            }
        })
    })
}

export function validateField(form, field) {
    return new Promise((resolve, reject) => {
        form.validateField(field,(valid) => {
            if (valid) {
                resolve(valid)
            } else {
                reject(valid)
            }
        })
    })
}

export function popMessage(title, message) {
    ElNotification({
        title: title,
        message: message,
    })
}

const storage = {
    store(key, object) {
        window.localStorage.setItem(key, JSON.stringify(object))
    },

    load(key) {
        const data = JSON.parse(window.localStorage.getItem(key))
        if (data === null)
            return {id: 0}
        return data
    },

    remove(key) {
        window.localStorage.removeItem(key)
    }
}

export default storage
