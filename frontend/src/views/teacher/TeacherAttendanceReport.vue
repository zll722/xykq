<template>
  <div class="teacher-report">
    <section class="page-hero">
      <div>
        <p class="page-tag">考勤报表</p>
        <h1>班级学生考勤统计</h1>
        <p>查看您授课班级的学生出勤情况，识别缺勤、迟到学生。</p>
      </div>
    </section>

    <el-card class="filter-card" shadow="hover" style="margin-bottom:18px">
      <div class="filter-row">
        <span class="filter-label">选择班级：</span>
        <el-select v-model="selectedClassId" placeholder="请选择班级" style="width:220px" @change="loadReport">
          <el-option
            v-for="cls in classes"
            :key="cls.classId"
            :label="cls.className"
            :value="cls.classId"
          />
        </el-select>
        <el-button type="primary" :disabled="!selectedClassId" @click="loadReport">
          <el-icon><Search /></el-icon>查询
        </el-button>
      </div>
    </el-card>

    <el-card class="table-card" shadow="hover" v-if="selectedClassId">
      <template #header>
        <div class="card-header">
          <div>
            <span class="card-title">{{ currentClassName }} — 考勤统计</span>
            <p>汇总该班级学生在您所授课程中的考勤记录</p>
          </div>
        </div>
      </template>

      <el-table :data="report" border stripe v-loading="reportLoading">
        <el-table-column label="学号" prop="studentNo" width="120" />
        <el-table-column label="姓名" prop="studentName" width="100" />
        <el-table-column label="出勤" width="80" align="center">
          <template #default="{ row }">
            <el-tag type="success" size="small">{{ row.presentCount ?? 0 }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="缺勤" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.absentCount > 0 ? 'danger' : 'info'" size="small">{{ row.absentCount ?? 0 }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="迟到" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.lateCount > 0 ? 'warning' : 'info'" size="small">{{ row.lateCount ?? 0 }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="请假" width="80" align="center">
          <template #default="{ row }">
            <el-tag type="info" size="small">{{ row.leaveCount ?? 0 }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="总记录" width="80" align="center">
          <template #default="{ row }">
            {{ row.totalCount ?? 0 }}
          </template>
        </el-table-column>
        <el-table-column label="出勤率" min-width="120">
          <template #default="{ row }">
            <el-progress
              :percentage="calcRate(row)"
              :color="rateColor(calcRate(row))"
              :stroke-width="10"
            />
          </template>
        </el-table-column>
        <template #empty>
          <el-empty description="暂无考勤记录" />
        </template>
      </el-table>
    </el-card>

    <el-card v-else class="empty-hint" shadow="never">
      <el-empty description="请先选择班级查看考勤报表" />
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, ref, computed } from 'vue';
import { ElMessage } from 'element-plus';
import { Search } from '@element-plus/icons-vue';
import request from '../../utils/request';

const classes = ref([]);
const selectedClassId = ref(null);
const report = ref([]);
const reportLoading = ref(false);

const currentClassName = computed(() => {
  const cls = classes.value.find(c => c.classId === selectedClassId.value);
  return cls?.className || '';
});

const calcRate = (row) => {
  const total = (row.totalCount ?? 0);
  if (total === 0) return 0;
  const present = (row.presentCount ?? 0) + (row.leaveCount ?? 0);
  return Math.round((present / total) * 100);
};

const rateColor = (rate) => {
  if (rate >= 90) return '#67c23a';
  if (rate >= 75) return '#e6a23c';
  return '#f56c6c';
};

const loadReport = async () => {
  if (!selectedClassId.value) return;
  reportLoading.value = true;
  try {
    const data = await request.get(`/teacher/classes/${selectedClassId.value}/attendance`);
    report.value = Array.isArray(data) ? data : [];
  } catch {
    ElMessage.error('加载报表失败');
  } finally {
    reportLoading.value = false;
  }
};

onMounted(async () => {
  try {
    const data = await request.get('/teacher/classes');
    classes.value = Array.isArray(data) ? data : [];
  } catch {
    ElMessage.error('加载班级失败');
  }
});
</script>

<style scoped>
.teacher-report { display: flex; flex-direction: column; gap: 0; }
.page-hero {
  margin-bottom: 24px; padding: 32px 36px; border-radius: 24px;
  background: linear-gradient(135deg, #143a63 0%, #275f9d 100%); color: #fff;
}
.page-tag { margin: 0 0 10px; font-size: 12px; letter-spacing: 0.1em; text-transform: uppercase; opacity: 0.72; }
.page-hero h1 { margin: 0 0 10px; font-size: 26px; font-weight: 700; }
.page-hero p { margin: 0; opacity: 0.82; }

.filter-card { border-radius: 16px; }
.filter-row { display: flex; align-items: center; gap: 12px; }
.filter-label { font-size: 14px; color: #344054; font-weight: 500; }

.table-card, .empty-hint { border-radius: 18px; }
.card-header { display: flex; align-items: center; justify-content: space-between; }
.card-title { font-size: 16px; font-weight: 600; color: #15334f; }
.card-header p { margin: 4px 0 0; font-size: 13px; color: #6b86a3; }
</style>
