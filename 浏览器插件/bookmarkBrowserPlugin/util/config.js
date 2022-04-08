// const baseUri = ;
var baseUri;

if (process.env.NODE_ENV === 'development') {
  baseUri = 'http://localhost:3000';
  // baseUri = 'https://bm.tapme.top';
} else {
  baseUri = 'https://bm.tapme.top';
}

const config = {
  baseUrl: baseUri + '/bookmark/api',
  ssoUrl: baseUri + '/userSpace/ssoAuth',
};

export default config;
