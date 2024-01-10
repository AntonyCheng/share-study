type DebouncedFn<T extends (...args: any[]) => any> = (this: ThisParameterType<T>, ...args: Parameters<T>) => void;

type ThrottledFn<T extends (...args: any[]) => any> = (this: ThisParameterType<T>, ...args: Parameters<T>) => void;

/**
 * @param {T} fn
 * @param {number} delay
 * @param {boolean} immediate
 * @return {ThrottledFn<T>}
 * @description 节流
 */
export function debounce<T extends (...args: any[]) => any>(fn: T, delay: number, immediate: boolean = false): DebouncedFn<T> {
  // let timer: number | null = null;
  let timer: NodeJS.Timeout | null = null;

  /**
   * @param { ThisParameterType<T>} this
   * @param {Parameters<T>} ...args
   * @return {*}
   * @description 闭包函数
   */
  return function (this: ThisParameterType<T>, ...args: Parameters<T>): any {
    // if (timer !== null) clearTimeout(timer);
    timer && clearTimeout(timer)
    if (immediate) {
      const callNow = !timer;
      timer = setTimeout(() => {
        timer = null;
      }, delay);
      callNow && fn.apply(this, args);
    } else {
      timer = setTimeout(() => {
        fn.apply(this, args);
      }, delay);
    }
  };
}

/**
 * @param {T} fn
 * @param {number} delay
 * @param {boolean} immediate
 * @return {ThrottledFn<T>}
 * @description 节流函数
 */
export function throttle<T extends (...args: any[]) => any>(fn: T, delay: number, immediate: boolean = false): ThrottledFn<T> {
  let lastCall = 0;

  /**
   * @param {this} this
   * @param {...args} ...args
   * @return {*}
   * @description 闭包函数
   */
  return function (this: ThisParameterType<T>, ...args: Parameters<T>): any {
    const now = new Date().getTime();
    // immediate 不为 true 时, 不立即执行
    lastCall === 0 && !immediate && (lastCall = now)
    const diff = now - lastCall;
    if (diff >= delay) {
      lastCall = now;
      fn.apply(this, args);
    }
  };
}

// export {
//   debounce,
//   throttle
// }
