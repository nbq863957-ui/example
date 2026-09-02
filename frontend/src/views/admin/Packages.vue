<template>
  <div class="page">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span>快递管理</span>
          <el-button :icon="Refresh" size="small" @click="fetchPackages">刷新</el-button>
        </div>
      </template>
      <el-table :data="packages" v-loading="loading" border stripe style="width: 100%">
        <el-table-column prop="id" label="快递ID" width="190" show-overflow-tooltip />
        <el-table-column prop="courier" label="快递公司" width="110" />
        <el-table-column prop="pickupCode" label="取件码" width="110" />
        <el-table-column prop="sender" label="寄件人" width="110" />
        <el-table-column prop="receiver" label="收件人" width="110" />
        <el-table-column prop="station" label="站点" width="120" />
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.pickedUp ? 'success' : 'info'">
              {{ row.pickedUp ? '已出库' : '未出库' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" width="170">
          <template #default="{ row }">{{ formatTime(row.createdAt) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right" align="center">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="toggleStatus(row)">
              {{ row.pickedUp ? '标记未出库' : '标记已出库' }}
            </el-button>
            <el-button type="danger" size="small" @click="removePackage(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Refresh } from '@element-plus/icons-vue'
import axios from 'axios'
import { useWebSocket } from '@/composables/useWebSocket'

const packages = ref([])
const loading = ref(false)

const fetchPackages = async () => {
  loading.value = true
  try {
    const { data } = await axios.get('/api/admin/packages')
    packages.value = data
  } catch (e) {
    ElMessage.error('获取快递失败')
  } finally {
    loading.value = false
  }
}

const toggleStatus = async (row) => {
  try {
    const { data } = await axios.patch(`/api/admin/package/${encodeURIComponent(row.id)}/status`, {
      isPickedUp: !row.pickedUp,
    })
    if (data.type === 'success') {
      ElMessage.success(data.message || '更新成功')
      fetchPackages()
    } else {
      ElMessage.error(data.message || '更新失败')
    }
  } catch (e) {
    ElMessage.error('更新失败')
  }
}

const removePackage = async (row) => {
  try {
    await ElMessageBox.confirm(`确定删除快递 "${row.id}" 吗？`, '提示', { type: 'warning' })
  } catch {
    return
  }
  try {
    const { data } = await axios.delete(`/api/admin/package/${encodeURIComponent(row.id)}`)
    if (data.type === 'success') {
      ElMessage.success(data.message || '删除成功')
      fetchPackages()
    } else {
      ElMessage.error(data.message || '删除失败')
    }
  } catch (e) {
    ElMessage.error('删除失败')
  }
}

// 后端 LocalDateTime 经 Jackson 序列化可能是数组 [y,m,d,h,m,s] 或 ISO 字符串，这里兼容两种
const formatTime = (t) => {
  if (!t) return '-'
  if (Array.isArray(t)) {
    const [y, m, d, h = 0, mi = 0, s = 0] = t
    return `${y}-${pad(m)}-${pad(d)} ${pad(h)}:${pad(mi)}:${pad(s)}`
  }
  return String(t).replace('T', ' ').slice(0, 19)
}
const pad = (n) => String(n).padStart(2, '0')

useWebSocket(() => fetchPackages())

onMounted(fetchPackages)
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
