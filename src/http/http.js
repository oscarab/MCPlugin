import axios from "axios";

axios.defaults.timeout = 2000 * 1000;
axios.defaults.baseURL = "/api";

const request = {

    get(url, config, success, failure) {
        return axios.get(url, config)
            .then(res => {
            return success ? success(res.data) : res.data
        })
            .catch(err => {
            return failure ? failure(err) : err
        })
    },

    post(url, data, success, failure) {
        return axios.post(url, data)
            .then(res => {
                return success ? success(res.data) : res.data
            })
            .catch(err => {
                return failure ? failure(err.response.data) : err.response.data
            })
    },

    upload(url, file) {
        let form_data = new FormData()
        form_data.append("file", file)
        return axios.post(url, form_data)
    },
}

export default request
