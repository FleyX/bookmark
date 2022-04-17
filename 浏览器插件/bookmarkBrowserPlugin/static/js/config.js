// var bookmarkHost = "https://fleyx.com";
var bookmarkHost = "http://localhost:8080";

var version = "0.1";

window.token = localStorage.getItem('token');
axios.defaults.baseURL = bookmarkHost + '/bookmark/api';
axios.defaults.headers.common['jwt-token'] = window.token;
axios.defaults.headers.post['Content-Type'] = 'application/json';
axios.defaults.headers.put['Content-Type'] = 'application/json';