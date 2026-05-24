<template>
  <div class="counselor-classes">
    <section class="page-hero">
      <div>
        <p class="page-tag">我的班级</p>
        <h1>负责班级与学生管理</h1>
        <p>查看您负责的班级信息及学生考勤概况。</p>
      </div>
      <div class="hero-note">
        <strong>{{ classes.length }}</strong>
        <span>个班级</span>
      </div>
    </section>

    <el-row :gutter="18" v-loading="loading">
      <el-col v-for="cls in classes" :key="cls.id" :xs="24" :sm="12" :lg="8" style="margin-bottom:18px">
        <el-card class="class-card" shadow="hover" @click="openClass(cls)">
          <div class="class-header">
            <div class="class-name">{{ cls.className }}</div>
            <el-tag v-if="cls.pendingLeaveCount > 0" type="warning" size="small">
              {{ cls.pendingLeaveCount }} 待审批
            </el-tag>
          </div>
          <div class="class-meta">
            <span>班级代码：{{ cls.classCode }}</span>
            <span>容纳人数：{{ cls.capacity }}</span>
            <span>在籍学生：{{ cls.studentCount }} 人</span>
          </div>
          <div class="class-footer">
            <el-button type="primary" link size="small" @click.stop="openClass(cls)">
              查看学生考勤 →
            </el-button>
          </div>
        </el-card>
      </el-col>
      <el-col v-if="!loading && classes.length === 0" :span="24">
        <el-empty description="暂无负责班级" />
      </el-col>
    </el-row>

    <!-- 班级学生考勤详情抽屉 -->
    <el-drawer v-model="drawerVisible" :title="currentClass?.className + ' — 学生考勤概况'" size="70%">
      <el-table :data="students" border stripe v-loading="studentsLoading">
        <el-table-column label="学号" prop="studentNo" width="120" />
        <el-table-column label="姓名" prop="realName" width="100" />
        <el-table-column label="联系电话" prop="phone" min-width="130" />
        <el-table-column label="出勤" width="80" align="center">
          <template #default="{ row }">
            <el-tag type="success" size="small">{{ row.presentCount ?? 0 }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="缺勤" width="80" align="center">
          <template #default="{ row }">
            <el-tag type="danger" size="small">{{ row.absentCount ?? 0 }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="迟到" width="80" align="center">
          <template #default="{ row }">
            <el-tag type="warning" size="small">{{ row.lateCount ?? 0 }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="请假" width="80" align="center">
          <template #default="{ row }">
            <el-tag type="info" size="small">{{ row.leaveCount ?? 0 }}</el-tag>
          </template>
        </el-table-column>
        <template #empty>
          <el-empty description="暂无学生数据" />
        </template>
      </el-table>
    </el-drawer>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue';
import { ElMessage } from 'element-plus';
import request from '../../utils/request';

const classes = ref([]);
const loading = ref(false);
const drawerVisible = ref(false);
const currentClass = ref(null);
const students = ref([]);
const studentsLoading = ref(false);

const loadClasses = async () => {
  loading.value = true;
  try {
    const data = await request.get('/counselor/classes');
    classes.value = Array.isArray(data) ? data : [];
  } catch {
    ElMessage.error('加载班级失败');
  } finally {
    loading.value = false;
  }
};

const openClass = async (cls) => {
  currentClass.value = cls;
  drawerVisible.value = true;
  studentsLoading.value = true;
  try {
    const data = await request.get(`/counselor/classes/${cls.id}/students`);
    students.value = Array.isArray(data) ? data : [];
  } catch {
    ElMessage.error('加载学生数据失败');
  } finally {
    studentsLoading.value = false;
  }
};

onMounted(loadClasses);
</script>

<style scoped>
.counselor-classes { display: flex; flex-direction: column; gap: 0; }
.page-hero {
  display: flex; align-items: flex-start; justify-content: space-between; gap: 24px;
  margin-bottom: 28px; padding: 32px 36px; border-radius: 24px;
  background: linear-gradient(135deg, #143a63 0%, #275f9d 100%); color: #fff;
}
.page-tag { margin: 0 0 10px; font-size: 12px; letter-spacing: 0.1em; text-transform: uppercase; opacity: 0.72; }
.page-hero h1 { margin: 0 0 10px; font-size: 26px; font-weight: 700; }
.page-hero p { margin: 0; opacity: 0.82; }
.hero-note {
  display: flex; flex-direction: column; align-items: center; gap: 4px;
  padding: 18px 28px; border-radius: 18px; background: rgba(255,255,255,0.12); white-space: nowrap;
}
.hero-note strong { font-size: 32px; font-weight: 800; }
.hero-note span { font-size: 13px; opacity: 0.8; }

.class-card { border-radius: 16px; cursor: pointer; transition: transform 0.15s, box-shadow 0.15s; }
.class-card:hover { transform: translateY(-2px); box-shadow: 0 8px 24px rgba(0,0,0,0.12) !important; }

.class-header { display: flex; align-items: center; justify-content: space-between; margin-bottom: 14px; }
.class-name { font-size: 16px; font-weight: 700; color: #15334f; }

.class-meta { display: flex; flex-direction: column; gap: 6px; font-size: 13px; color: #6b86a3; margin-bottom: 14px; }

.class-footer { border-top: 1px solid #eef0f3; padding-top: 10px; }
</style>
