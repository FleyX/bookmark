import PageIndex from './pages/Index';
import Login from './pages/public/Login';

export default [
  {
    path: '/',
    component: PageIndex,
  },
  {
    path: '/public/login',
    component: Login,
  },
];
