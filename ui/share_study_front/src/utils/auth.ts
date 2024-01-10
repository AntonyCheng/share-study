
// token鉴权
// import Cookies from 'js-cookie'

// const TokenKeyUSER = 'userLoginState'
const TokenKey: string = 'adminLoginstate'

export function getToken(): string | null {
  // return Cookies.get(TokenKey)
  return localStorage.getItem(TokenKey)
}

export function setToken(token: string): void {
  // return Cookies.set(TokenKey, token)
  localStorage.setItem(TokenKey, token)
}

export function removeToken(): void {
  // return Cookies.remove(TokenKey)
  localStorage.removeItem(TokenKey)
}
