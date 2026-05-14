<template>
  <div class="attendance-adjust-page">
    <section class="page-hero">
      <div>
        <p class="hero-tag">考勤修正</p>
        <h1>先找到记录，再决定如何修正</h1>
        <p>不再让管理员记住记录 ID。先在页面里检索记录，再基于上下文完成补录或修正。</p>
      </div>
      <div class="hero-side">
        <strong>{{ records.length }}</strong>
        <span>条可操作记录</span>
      </div>
    </section>

    <el-card class="filter-card" shadow="hover">
      <el-form :inline="true" class="filter-form">
        <el-form-item label="课程ID">
          <el-input v-model="filters.courseId" placeholder="可选" />
        </el-form-item>
        <el-form-item label="班级ID">
          <el-input v-model="filters.classId" placeholder="可选" />
        </el-form-item>
        <el-form-item label="考勤日期">
          <el-date-picker v-model="filters.attendanceDate" type="date" value-format="YYYY-MM-DD" placeholder="选择日期" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadRecords">查询记录</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <div>
            <span class="title">可修正考勤记录</span>
            <p>先定位记录，再点击修正，不需要手工记忆或输入记录 ID</p>
          </div>
        </div>
      </template>

      <el-table :data="records" border stripe>
        <el-table-column label="记录ID" prop="id" width="90" />
        <el-table-column label="课程" prop="courseName" min-width="160" />
        <el-table-column label="班级" prop="className" min-width="180" />
        <el-table-column label="学生" min-width="140">
          <template #default="{ row }">
            {{ row.studentName || `学生#${row.studentId}` }}
          </template>
        </el-table-column>
        <el-table-column label="日期" prop="attendanceDate" width="120" />
        <el-table-column label="当前状态" prop="statusText" width="120" />
        <el-table-column label="签到时间" prop="signTime" min-width="160" />
        <el-table-column label="操作" width="140" fixed="right">
          <template #default="scope">
            <el-button type="primary" @click="openAdjust(scope.row)">修正</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="visible" title="修正考勤记录" width="520px">
      <el-form label-position="top">
        <el-form-item label="当前记录">
          <el-alert :title="selectedSummary" type="info" :closable="false" />
        </el-form-item>
        <el-form-item label="目标状态">
          <el-select v-model="form.afterStatus" placeholder="请选择目标状态" style="width: 100%">
            <el-option label="正常" value="PRESENT" />
            <el-option label="迟到" value="LATE" />
            <el-option label="缺勤" value="ABSENT" />
            <el-option label="请假" value="LEAVE" />
          </el-select>
        </el-form-item>
        <el-form-item label="修正原因">
          <el-input v-model="form.reason" type="textarea" :rows="4" maxlength="200" show-word-limit placeholder="例如：系统故障未签到、请假审批通过后同步修正等" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="visible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submit">提交修正</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue';
import { useRoute } from 'vue-router';
import { ElMessage } from 'element-plus';
import { listAdminAttendanceRecordsApi } from '../../api/adminStats';
import { adjustAttendanceRecordApi } from '../../api/adminAttendanceManage';

const route = useRoute();
const filters = reactive({
  courseId: '',
  classId: '',
  attendanceDate: ''
});
const records = ref([]);
const visible = ref(false);
const submitting = ref(false);
const selected = ref(null);
const form = reactive({
  afterStatus: 'PRESENT',
  reason: ''
});

const selectedSummary = computed(() => {
  if (!selected.value) return '未选择记录';
  const student = selected.value.studentName || `学生#${selected.value.studentId}`;
  return `记录 #${selected.value.id} · ${student} · ${selected.value.courseName || '未命名课程'} · ${selected.value.className || '未命名班级'} · 当前状态 ${selected.value.statusText || '-'}`;
});

const loadRecords = async () => {
  records.value = await listAdminAttendanceRecordsApi({ ...filters });
};

const applyRouteFilters = () => {
  filters.courseId = route.query.courseId || '';
  filters.classId = route.query.classId || '';
  filters.attendanceDate = route.query.attendanceDate || '';
};

const openAdjust = (row) => {
  selected.value = row;
  form.afterStatus = 'PRESENT';
  form.reason = '';
  visible.value = true;
};

const submit = async () => {
  if (!selected.value) return;
  submitting.value = true;
  try {
    await adjustAttendanceRecordApi(selected.value.id, {
      afterStatus: form.afterStatus,
      reason: form.reason
    });
    ElMessage.success('考勤记录已修正');
    visible.value = false;
    loadRecords();
  } catch (error) {
    console.error(error);
    ElMessage.error('提交失败，请稍后重试');
  } finally {
    submitting.value = false;
  }
};

onMounted(() => {
  applyRouteFilters();
  loadRecords();
});
</script>

<style scoped>
.attendance-adjust-page {
  display: grid;
  gap: 18px;
}

.page-hero {
  display: flex;
  justify-content: space-between;
  gap: 18px;
  padding: 26px 28px;
  border-radius: 28px;
  background: linear-gradient(135deg, #112d49 0%, #1c4b83 55%, #2d768b 100%);
  color: #fff;
}

.hero-tag {
  margin: 0 0 8px;
  font-size: 12px;
  letter-spacing: 0.14em;
  text-transform: uppercase;
  color: rgba(255, 255, 255, 0.72);
}

.page-hero h1 {
  margin: 0;
  color: #fff;
}

.page-hero p {
  margin: 10px 0 0;
  max-width: 620px;
  color: rgba(255, 255, 255, 0.82);
}

.hero-side {
  min-width: 160px;
  padding: 18px;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.12);
}

.hero-side strong,
.hero-side span {
  display: block;
}

.hero-side strong {
  font-size: 32px;
}

.filter-card,
.table-card {
  border-radius: 24px;
  border: none;
}

.filter-form {
  display: flex;
  flex-wrap: wrap;
  gap: 12px 0;
}

.card-header p {
  margin: 6px 0 0;
  color: #7890a8;
}

.title {
  font-size: 18px;
  font-weight: 700;
  color: #17324d;
}
</style>
