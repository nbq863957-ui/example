import { onBeforeUnmount } from 'vue'

// 后台数据实时刷新：连接后端 ws，收到任意广播即触发回调重新拉取数据
// 用法：useWebSocket(() => fetchXxx())
export function useWebSocket(onMessage, { url = 'ws://127.0.0.1:8080/ws', retry = 3000 } = {}) {
  let ws = null
  let retryTimer = null
  let stopped = false

  const connect = () => {
    if (stopped) return
    if (ws && (ws.readyState === WebSocket.OPEN || ws.readyState === WebSocket.CONNECTING)) {
      return
    }
    try {
      ws = new WebSocket(url)
    } catch (e) {
      scheduleReconnect()
      return
    }
    ws.onmessage = (event) => {
      try {
        onMessage(event)
      } catch (e) {
        /* ignore */
      }
    }
    ws.onclose = () => {
      ws = null
      scheduleReconnect()
    }
    ws.onerror = () => {
      try {
        ws && ws.close()
      } catch (e) {
        /* ignore */
      }
    }
  }

  const scheduleReconnect = () => {
    if (stopped) return
    clearTimeout(retryTimer)
    retryTimer = setTimeout(connect, retry)
  }

  const stop = () => {
    stopped = true
    clearTimeout(retryTimer)
    if (ws) {
      ws.onclose = null
      ws.onerror = null
      try {
        ws.close()
      } catch (e) {
        /* ignore */
      }
      ws = null
    }
  }

  connect()
  onBeforeUnmount(stop)

  return { stop }
}
