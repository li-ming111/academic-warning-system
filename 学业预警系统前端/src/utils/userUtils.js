/**
 * 获取当前登录用户的ID（数字类型）
 * @returns {number|null} 用户ID，如果未登录则返回null
 */
export const getUserId = () => {
  const userIdStr = localStorage.getItem('userId')
  return userIdStr ? parseInt(userIdStr) : null
}

/**
 * 获取当前登录用户名
 * @returns {string|null} 用户名
 */
export const getUsername = () => {
  return localStorage.getItem('username')
}

/**
 * 获取当前登录用户的角色
 * @returns {string|null} 用户角色
 */
export const getUserRole = () => {
  return localStorage.getItem('role')
}

/**
 * 检查用户是否已登录
 * @returns {boolean} 是否已登录
 */
export const isUserLoggedIn = () => {
  return !!localStorage.getItem('token')
}
