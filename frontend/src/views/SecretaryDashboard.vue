<template>
  <div class="secretary-dashboard page-shell">
    <div class="welcome-banner" v-if="activeTab === 'create'">
      <div class="welcome-text">
        <h2>{{ t('secretary.welcome') }}</h2>
        <p>{{ t('secretary.welcomeDesc') }}</p>
      </div>
      <div class="welcome-stats">
         <div class="stat-item">
            <span class="stat-value">{{ courses.length }}</span>
            <span class="stat-label">{{ t('secretary.registeredCourses') }}</span>
         </div>
         <div class="stat-item">
            <span class="stat-value">{{ teachers.length }}</span>
            <span class="stat-label">{{ t('secretary.totalStaff') }}</span>
         </div>
      </div>
    </div>

    <el-card shadow="never" class="admin-card">
      <div class="admin-panel">
          <section v-if="activeTab === 'create'">
              <div class="tab-content planning-layout">
                <section class="tab-hero compact-hero">
                  <div>
                    <div class="tab-eyebrow">Planning Studio</div>
                    <h3>{{ t('secretary.tabs.createCourse') }}</h3>
                    <p>集中完成课程基础信息、授课安排与教学目标权重配置，减少分散录入带来的遗漏。</p>
                  </div>
                  <div class="tab-hero-stats">
                    <div class="mini-stat">
                      <strong>{{ courseObjectives.length }}</strong>
                      <span>{{ t('secretary.createCourse.objectivesTitle') }}</span>
                    </div>
                    <div class="mini-stat">
                      <strong>{{ teachers.length }}</strong>
                      <span>{{ t('secretary.totalStaff') }}</span>
                    </div>
                  </div>
                </section>

                <el-form :model="newCourse" label-width="140px" class="course-form" label-position="left">
                    <div class="form-panel">
                    <div class="form-section-title">{{ t('secretary.createCourse.basicInfo') }}</div>
                    <el-row :gutter="40">
                        <el-col :span="12">
                            <el-form-item :label="t('secretary.createCourse.courseCode')">
                                <el-input v-model="newCourse.courseCode" :placeholder="t('secretary.createCourse.codePlaceholder')" />
                            </el-form-item>
                        </el-col>
                        <el-col :span="12">
                            <el-form-item :label="t('secretary.createCourse.courseName')">
                                <el-input v-model="newCourse.courseName" :placeholder="t('secretary.createCourse.namePlaceholder')" />
                            </el-form-item>
                        </el-col>
                    </el-row>
                    <el-row :gutter="40">
                        <el-col :span="12">
                            <el-form-item :label="t('secretary.createCourse.semester')">
                                <el-input v-model="newCourse.semester" :placeholder="t('secretary.createCourse.semesterPlaceholder')" />
                            </el-form-item>
                        </el-col>
                        <el-col :span="12">
                            <el-form-item :label="t('secretary.createCourse.bindTeacher')">
                                <el-select v-model="newCourse.teacherId" filterable :placeholder="t('secretary.createCourse.teacherPlaceholder')" style="width: 100%">
                                    <el-option v-for="t in teachers" :key="t.id" :label="t.name + ' (' + t.id + ')'" :value="t.id" />
                                </el-select>
                            </el-form-item>
                        </el-col>
                    </el-row>
                    <el-row :gutter="40">
                        <el-col :span="12">
                            <el-form-item :label="t('course.classTime')">
                                <el-select v-model="newCourse.classTime" :placeholder="t('course.classTimePlaceholder')" style="width:100%">
                                    <el-option label="周一 第1-2节" value="周一 第1-2节"></el-option>
                                    <el-option label="周一 第3-4节" value="周一 第3-4节"></el-option>
                                    <el-option label="周一 第5-6节" value="周一 第5-6节"></el-option>
                                    <el-option label="周一 第7-8节" value="周一 第7-8节"></el-option>
                                    <el-option label="周二 第1-2节" value="周二 第1-2节"></el-option>
                                    <el-option label="周二 第3-4节" value="周二 第3-4节"></el-option>
                                    <el-option label="周二 第5-6节" value="周二 第5-6节"></el-option>
                                    <el-option label="周二 第7-8节" value="周二 第7-8节"></el-option>
                                    <el-option label="周三 第1-2节" value="周三 第1-2节"></el-option>
                                    <el-option label="周三 第3-4节" value="周三 第3-4节"></el-option>
                                    <el-option label="周三 第5-6节" value="周三 第5-6节"></el-option>
                                    <el-option label="周三 第7-8节" value="周三 第7-8节"></el-option>
                                    <el-option label="周四 第1-2节" value="周四 第1-2节"></el-option>
                                    <el-option label="周四 第3-4节" value="周四 第3-4节"></el-option>
                                    <el-option label="周四 第5-6节" value="周四 第5-6节"></el-option>
                                    <el-option label="周四 第7-8节" value="周四 第7-8节"></el-option>
                                    <el-option label="周五 第1-2节" value="周五 第1-2节"></el-option>
                                    <el-option label="周五 第3-4节" value="周五 第3-4节"></el-option>
                                    <el-option label="周五 第5-6节" value="周五 第5-6节"></el-option>
                                    <el-option label="周五 第7-8节" value="周五 第7-8节"></el-option>
                                </el-select>
                            </el-form-item>
                        </el-col>
                        <el-col :span="12">
                            <el-form-item :label="t('course.classLocation')">
                                <el-select v-model="newCourse.location" :placeholder="t('course.classLocationPlaceholder')" style="width:100%">
                                    <el-option label="教一101" value="教一101"></el-option>
                                    <el-option label="教一102" value="教一102"></el-option>
                                    <el-option label="教一201" value="教一201"></el-option>
                                    <el-option label="教一202" value="教一202"></el-option>
                                    <el-option label="实验楼A区301" value="实验楼A区301"></el-option>
                                    <el-option label="实验楼A区302" value="实验楼A区302"></el-option>
                                    <el-option label="四教404" value="四教404"></el-option>
                                    <el-option label="四教405" value="四教405"></el-option>
                                </el-select>
                            </el-form-item>
                        </el-col>
                    </el-row>
                    </div>

                    <div class="form-panel objective-panel">
                    <div class="form-section-title with-helper">
                      <span>{{ t('secretary.createCourse.objectivesTitle') }}</span>
                      <small>{{ t('secretary.createCourse.objectivesTip') }}</small>
                    </div>
                    
                    <div v-for="(obj, index) in courseObjectives" :key="index" class="objective-card">
                        <div class="objective-header">
                        <span class="objective-index">{{ t('secretary.createCourse.objectiveLabelWithIndex', { index: obj.objectiveIndex }) }}</span>
                            <el-button type="danger" link @click="removeObjectiveRow(index)" v-if="courseObjectives.length > 1">
                          <el-icon><Delete /></el-icon> {{ t('secretary.createCourse.removeObj') }}
                            </el-button>
                        </div>
                      <el-form-item :label="t('secretary.createCourse.objDescLabel')" label-width="80px">
                        <el-input type="textarea" v-model="obj.description" :rows="2" :placeholder="t('secretary.createCourse.objDescPlaceholder')" />
                        </el-form-item>
                        <el-row :gutter="20">
                            <el-col :span="6">
                          <el-form-item :label="t('secretary.createCourse.regularWeight')">
                                    <el-input-number v-model="obj.regularWeight" :min="0" :max="1" :step="0.05" size="small" style="width:100%" />
                                </el-form-item>
                            </el-col>
                            <el-col :span="6">
                          <el-form-item :label="t('secretary.createCourse.labWeight')">
                                    <el-input-number v-model="obj.labWeight" :min="0" :max="1" :step="0.05" size="small" style="width:100%" />
                                </el-form-item>
                            </el-col>
                            <el-col :span="6">
                          <el-form-item :label="t('secretary.createCourse.testWeight')">
                                    <el-input-number v-model="obj.testWeight" :min="0" :max="1" :step="0.05" size="small" style="width:100%" />
                                </el-form-item>
                            </el-col>
                            <el-col :span="6">
                          <el-form-item :label="t('secretary.createCourse.finalWeight')">
                                    <el-input-number v-model="obj.finalWeight" :min="0" :max="1" :step="0.05" size="small" style="width:100%" />
                                </el-form-item>
                            </el-col>
                        </el-row>
                    </div>

                    <el-button type="primary" plain @click="addObjectiveRow" class="add-obj-btn">
                      <el-icon><Plus /></el-icon> {{ t('secretary.createCourse.addObj') }}
                    </el-button>
                    </div>

                    <div class="submit-action">
                      <el-button type="primary" size="large" @click="createCourse" class="primary-submit-btn">
                            <el-icon><Check /></el-icon> {{ t('secretary.createCourse.submitBtn') }}
                        </el-button>
                    </div>
                </el-form>
              </div>
              </section>

              <section v-else-if="activeTab === 'enroll'">
               <div class="tab-content enrollment-layout">
                 <section class="tab-hero compact-hero">
                   <div>
                     <div class="tab-eyebrow">Enrollment Desk</div>
                     <h3>{{ t('secretary.tabs.enrollment') }}</h3>
                     <p>快速完成学生选课与绑定，减少重复查找课程和学生编号的操作成本。</p>
                   </div>
                   <div class="tab-hero-stats">
                     <div class="mini-stat">
                       <strong>{{ courses.length }}</strong>
                       <span>{{ t('secretary.registeredCourses') }}</span>
                     </div>
                     <div class="mini-stat">
                       <strong>{{ enrollment.courseId ? 1 : 0 }}</strong>
                       <span>{{ t('secretary.enroll.targetCourse') }}</span>
                     </div>
                   </div>
                 </section>
                 <div class="enroll-box section-surface">
                    <h3 class="box-title">{{ t('secretary.enroll.title') }}</h3>
                    <p class="box-subtitle">先选择目标课程，再输入学号完成单个学生的快速绑定。</p>
                    <el-form label-position="top">
                        <el-form-item :label="t('secretary.enroll.targetCourse')">
                            <el-select v-model="enrollment.courseId" filterable :placeholder="t('secretary.enroll.searchCourse')" size="large" style="width: 100%">
                                <el-option v-for="c in courses" :key="c.id" :label="c.courseName + ' (' + c.semester + ')'" :value="c.id" />
                            </el-select>
                        </el-form-item>
                        <el-form-item :label="t('secretary.enroll.studentId')">
                            <el-input v-model="enrollment.studentId" :placeholder="t('secretary.enroll.studentIdPlaceholder')" size="large" clearable>
                               <template #prefix><el-icon><Avatar /></el-icon></template>
                            </el-input>
                        </el-form-item>
                        <el-button type="success" @click="enrollStudent" size="large" style="width: 100%; margin-top: 10px;">
                           {{ t('secretary.enroll.confirmBtn') }}
                        </el-button>
                    </el-form>
                 </div>
               </div>
          </section>
          
          <section v-else-if="activeTab === 'list'">
              <div class="tab-content">
                  <section class="tab-hero compact-hero">
                    <div>
                      <div class="tab-eyebrow">Course Overview</div>
                      <h3>{{ t('secretary.tabs.courseList') }}</h3>
                      <p>汇总查看课程编码、学期、授课教师等关键信息，便于排课和资源协调。</p>
                    </div>
                    <div class="tab-hero-stats">
                      <div class="mini-stat">
                        <strong>{{ courses.length }}</strong>
                        <span>{{ t('secretary.registeredCourses') }}</span>
                      </div>
                      <div class="mini-stat">
                        <strong>{{ uniqueSemesterCount }}</strong>
                        <span>{{ t('secretary.list.semester') }}</span>
                      </div>
                    </div>
                  </section>
                  <section class="table-shell">
                    <div class="table-shell-header">
                      <div>
                        <h4>课程总览</h4>
                        <p>按课程编码、名称和授课教师快速核对当前已创建课程。</p>
                      </div>
                    </div>
                    <el-table :data="pagedCourses" stripe class="secretary-table">
                      <el-table-column prop="courseCode" :label="t('secretary.list.courseCode')" width="150" align="center">
                         <template #default="scope">
                            <el-tag type="info">{{ scope.row.courseCode }}</el-tag>
                         </template>
                      </el-table-column>
                      <el-table-column prop="courseName" :label="t('secretary.list.courseName')" show-overflow-tooltip>
                         <template #default="scope">
                            <span style="font-weight: 500;">{{ scope.row.courseName }}</span>
                         </template>
                      </el-table-column>
                      <el-table-column prop="semester" :label="t('secretary.list.semester')" width="180" align="center" />
                      <el-table-column prop="teacherId" :label="t('secretary.list.teacherId')" width="120" align="center">
                          <template #default="scope">
                            <el-tag type="warning" effect="plain">ID: {{ scope.row.teacherId }}</el-tag>
                         </template>
                      </el-table-column>
                      <el-table-column :label="t('secretary.list.status')" width="100" align="center">
                         <template #default>
                             <el-badge is-dot type="success" class="custom-badge" /> {{ t('secretary.list.normal') }}
                         </template>
                      </el-table-column>
                      <el-table-column :label="t('common.actions')" width="240" align="center">
                        <template #default="scope">
                          <div class="action-row">
                            <el-button type="primary" link @click="openEditDialog(scope.row)">{{ t('secretary.management.editCourse') }}</el-button>
                            <el-button type="warning" link @click="openStudentBind(scope.row)">{{ t('secretary.management.bindPeople') }}</el-button>
                            <el-button type="danger" link @click="deleteCourse(scope.row.id)">{{ t('secretary.management.deleteCourse') }}</el-button>
                          </div>
                        </template>
                      </el-table-column>
                  </el-table>
                    <div class="table-pagination">
                      <el-pagination
                        v-model:current-page="coursePage"
                        v-model:page-size="coursePageSize"
                        :page-sizes="[5, 10, 20]"
                        layout="total, sizes, prev, pager, next"
                        :total="courses.length"
                      />
                    </div>
                  </section>
              </div>
          </section>

          <section v-else-if="activeTab === 'approve'">
              <div class="tab-content">
                  <section class="tab-hero compact-hero">
                    <div>
                      <div class="tab-eyebrow">Approval Queue</div>
                      <h3>{{ t('secretary.tabs.approveUsers') }}</h3>
                      <p>集中审批新用户注册申请，并在审批时直接指定用户身份角色。</p>
                    </div>
                    <div class="tab-hero-stats">
                      <div class="mini-stat alert-stat">
                        <strong>{{ pendingUsers.length }}</strong>
                        <span>{{ t('common.pending') }}</span>
                      </div>
                    </div>
                  </section>
                  <section class="table-shell">
                    <div class="table-shell-header">
                      <div>
                        <h4>{{ t('secretary.approve.title') }}</h4>
                        <p>审批结果会立即生效，支持在同一列表中完成通过、驳回和角色分配。</p>
                      </div>
                    </div>
                  <el-table :data="pagedPendingUsers" stripe class="secretary-table">
                      <el-table-column prop="username" :label="t('secretary.approve.username')" align="center" />
                      <el-table-column prop="name" :label="t('secretary.approve.name')" align="center" />
                      <el-table-column prop="email" label="邮箱" align="center" />
                      <el-table-column prop="role" :label="t('secretary.approve.role')" align="center">
                          <template #default="scope">
                          <el-select v-model="scope.row.approvedRole" size="small" style="width: 120px;">
                            <el-option :label="t('role.teacher')" value="TEACHER" />
                            <el-option :label="t('role.student')" value="STUDENT" />
                          </el-select>
                          </template>
                      </el-table-column>
                      <el-table-column :label="t('secretary.approve.action')" align="center" width="200">
                          <template #default="scope">
                          <el-button type="success" size="small" @click="handleApproveUser(scope.row, 'APPROVED')">{{ t('secretary.approve.btnApprove') }}</el-button>
                          <el-button type="danger" size="small" @click="handleApproveUser(scope.row, 'REJECTED')">{{ t('secretary.approve.btnReject') }}</el-button>
                          </template>
                      </el-table-column>
                    </el-table>
                    <div class="table-pagination">
                      <el-pagination
                        v-model:current-page="pendingPage"
                        v-model:page-size="pendingPageSize"
                        :page-sizes="[5, 10, 20]"
                        layout="total, sizes, prev, pager, next"
                        :total="pendingUsers.length"
                      />
                    </div>
                    </section>
                  </div>
                </section>

          <section v-else-if="activeTab === 'teachers'" class="tab-content">
            <section class="tab-hero compact-hero">
              <div>
                <div class="tab-eyebrow">Faculty Directory</div>
                <h3>{{ t('secretary.management.teacherTitle') }}</h3>
                <p>{{ t('secretary.management.teacherDesc') }}</p>
              </div>
              <div class="tab-hero-stats">
                <div class="mini-stat">
                  <strong>{{ teachers.length }}</strong>
                  <span>{{ t('secretary.totalStaff') }}</span>
                </div>
              </div>
            </section>
            <section class="table-shell">
              <div class="table-shell-header">
                <div>
                  <h4>{{ t('secretary.management.teacherTitle') }}</h4>
                  <p>查看当前已配置的教师账号与身份信息。</p>
                </div>
                <el-button type="primary" @click="openUserDialog('TEACHER')">{{ t('secretary.management.addTeacher') }}</el-button>
              </div>
              <el-table :data="pagedTeachers" stripe class="secretary-table">
                <el-table-column prop="id" label="ID" width="100" align="center" />
                <el-table-column prop="username" :label="t('secretary.approve.username')" align="center" />
                <el-table-column prop="name" :label="t('secretary.approve.name')" align="center" />
                <el-table-column prop="email" label="邮箱" align="center" />
                <el-table-column :label="t('secretary.approve.role')" width="140" align="center">
                  <template #default>
                    <el-tag type="success">{{ t('role.teacher') }}</el-tag>
                  </template>
                </el-table-column>
                <el-table-column :label="t('common.actions')" width="220" align="center">
                  <template #default="scope">
                    <div class="action-row">
                      <el-button type="primary" link @click="openUserDialog('TEACHER', scope.row)">{{ t('secretary.management.editUser') }}</el-button>
                      <el-button type="danger" link @click="deleteUser('TEACHER', scope.row)">{{ t('secretary.management.deleteUser') }}</el-button>
                    </div>
                  </template>
                </el-table-column>
              </el-table>
              <div class="table-pagination">
                <el-pagination
                  v-model:current-page="teacherPage"
                  v-model:page-size="teacherPageSize"
                  :page-sizes="[5, 10, 20]"
                  layout="total, sizes, prev, pager, next"
                  :total="teachers.length"
                />
              </div>
            </section>
          </section>

          <section v-else-if="activeTab === 'students'" class="tab-content">
            <section class="tab-hero compact-hero">
              <div>
                <div class="tab-eyebrow">Student Directory</div>
                <h3>{{ t('secretary.management.studentTitle') }}</h3>
                <p>{{ t('secretary.management.studentDesc') }}</p>
              </div>
              <div class="tab-hero-stats">
                <div class="mini-stat">
                  <strong>{{ students.length }}</strong>
                  <span>{{ t('role.student') }}</span>
                </div>
              </div>
            </section>
            <section class="table-shell">
              <div class="table-shell-header">
                <div>
                  <h4>{{ t('secretary.management.studentTitle') }}</h4>
                  <p>集中查看学生基础账号信息，便于配合选课绑定与课程组织。</p>
                </div>
                <el-button type="primary" @click="openUserDialog('STUDENT')">{{ t('secretary.management.addStudent') }}</el-button>
              </div>
              <el-table :data="pagedStudents" stripe class="secretary-table">
                <el-table-column prop="id" label="ID" width="100" align="center" />
                <el-table-column prop="username" :label="t('secretary.approve.username')" align="center" />
                <el-table-column prop="name" :label="t('secretary.approve.name')" align="center" />
                <el-table-column prop="email" label="邮箱" align="center" />
                <el-table-column :label="t('secretary.approve.role')" width="140" align="center">
                  <template #default>
                    <el-tag type="info">{{ t('role.student') }}</el-tag>
                  </template>
                </el-table-column>
                <el-table-column :label="t('common.actions')" width="220" align="center">
                  <template #default="scope">
                    <div class="action-row">
                      <el-button type="primary" link @click="openUserDialog('STUDENT', scope.row)">{{ t('secretary.management.editUser') }}</el-button>
                      <el-button type="danger" link @click="deleteUser('STUDENT', scope.row)">{{ t('secretary.management.deleteUser') }}</el-button>
                    </div>
                  </template>
                </el-table-column>
              </el-table>
              <div class="table-pagination">
                <el-pagination
                  v-model:current-page="studentPage"
                  v-model:page-size="studentPageSize"
                  :page-sizes="[5, 10, 20]"
                  layout="total, sizes, prev, pager, next"
                  :total="students.length"
                />
              </div>
            </section>
          </section>

          <section v-else class="tab-content">
            <el-empty :description="t('common.noData')" />
          </section>
      </div>
    </el-card>

    <el-dialog v-model="courseDialogVisible" :title="isEdit ? '编辑课程' : '新建课程'" width="500px">
      <el-form :model="editingCourse" label-width="100px">
        <el-form-item label="课程代码">
          <el-input v-model="editingCourse.courseCode" />
        </el-form-item>
        <el-form-item label="课程名称">
          <el-input v-model="editingCourse.courseName" />
        </el-form-item>
        <el-form-item label="学期">
          <el-input v-model="editingCourse.semester" />
        </el-form-item>
        <el-form-item :label="t('course.classTime')">
          <el-select v-model="editingCourse.classTime" :placeholder="t('course.classTimePlaceholder')" style="width:100%">
            <el-option label="周一 第1-2节" value="周一 第1-2节"></el-option>
            <el-option label="周一 第3-4节" value="周一 第3-4节"></el-option>
            <el-option label="周一 第5-6节" value="周一 第5-6节"></el-option>
            <el-option label="周一 第7-8节" value="周一 第7-8节"></el-option>
            <el-option label="周二 第1-2节" value="周二 第1-2节"></el-option>
            <el-option label="周二 第3-4节" value="周二 第3-4节"></el-option>
            <el-option label="周二 第5-6节" value="周二 第5-6节"></el-option>
            <el-option label="周二 第7-8节" value="周二 第7-8节"></el-option>
            <el-option label="周三 第1-2节" value="周三 第1-2节"></el-option>
            <el-option label="周三 第3-4节" value="周三 第3-4节"></el-option>
            <el-option label="周三 第5-6节" value="周三 第5-6节"></el-option>
            <el-option label="周三 第7-8节" value="周三 第7-8节"></el-option>
            <el-option label="周四 第1-2节" value="周四 第1-2节"></el-option>
            <el-option label="周四 第3-4节" value="周四 第3-4节"></el-option>
            <el-option label="周四 第5-6节" value="周四 第5-6节"></el-option>
            <el-option label="周四 第7-8节" value="周四 第7-8节"></el-option>
            <el-option label="周五 第1-2节" value="周五 第1-2节"></el-option>
            <el-option label="周五 第3-4节" value="周五 第3-4节"></el-option>
            <el-option label="周五 第5-6节" value="周五 第5-6节"></el-option>
            <el-option label="周五 第7-8节" value="周五 第7-8节"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item :label="t('course.classLocation')">
          <el-select v-model="editingCourse.location" :placeholder="t('course.classLocationPlaceholder')" style="width:100%">
            <el-option label="教一101" value="教一101"></el-option>
            <el-option label="教一102" value="教一102"></el-option>
            <el-option label="教一201" value="教一201"></el-option>
            <el-option label="教二101" value="教二101"></el-option>
            <el-option label="教二201" value="教二201"></el-option>
            <el-option label="教二301" value="教二301"></el-option>
            <el-option label="实验楼101" value="实验楼101"></el-option>
            <el-option label="实验楼202" value="实验楼202"></el-option>
            <el-option label="体育馆" value="体育馆"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="courseDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveCourse">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="studentBindVisible" title="关联教师与学生" width="600px">
      <el-form label-width="100px">
        <el-form-item label="授课教师">
          <el-select v-model="editingCourse.teacherId" placeholder="选择教师">
            <el-option v-for="t in teachers" :key="t.id" :label="t.name" :value="t.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="管理学生">
          <el-transfer
            v-model="boundStudents"
            :data="allStudentsList"
            :titles="['未选中', '已选中']"
            filterable
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="studentBindVisible = false">取消</el-button>
        <el-button type="primary" @click="saveBindPeople">保存关联</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="userDialogVisible" :title="userDialogTitle" width="520px">
      <el-form :model="userForm" label-width="100px">
        <el-form-item :label="t('secretary.approve.username')">
          <el-input v-model="userForm.username" />
        </el-form-item>
        <el-form-item :label="t('secretary.approve.name')">
          <el-input v-model="userForm.name" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="userForm.email" />
        </el-form-item>
        <el-form-item :label="t('login.password')">
          <el-input v-model="userForm.password" type="password" show-password :placeholder="userDialogMode === 'edit' ? t('secretary.management.passwordPlaceholder') : ''" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="userDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitUserForm">{{ userDialogMode === 'create' ? t('secretary.management.createUser') : t('secretary.management.saveUser') }}</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useI18n } from 'vue-i18n'
import { Plus, User, DocumentCopy, Connection, Delete, Avatar, Check } from '@element-plus/icons-vue'

const { t } = useI18n({ useScope: 'global' })
const courses = ref([])
const teachers = ref([])
const students = ref([])
const route = useRoute()
const router = useRouter()
const validTabs = ['create', 'enroll', 'list', 'approve', 'teachers', 'students']
const activeTab = ref(validTabs.includes(route.query.tab) ? route.query.tab : 'create')

watch(() => route.query.tab, (newTab) => {
  activeTab.value = validTabs.includes(newTab) ? newTab : 'create'
})

const newCourse = ref({
    courseCode: '',
    courseName: '',
    semester: '',
    teacherId: '',
    classTime: '',
    location: ''
})
const courseObjectives = ref([
    { objectiveIndex: 1, description: '', regularWeight: 0.2, testWeight: 0.2, labWeight: 0.3, finalWeight: 0.3 }
])

const enrollment = ref({
    courseId: '',
    studentId: ''
})

const pendingUsers = ref([])
const coursePage = ref(1)
const coursePageSize = ref(10)
const pendingPage = ref(1)
const pendingPageSize = ref(10)
const teacherPage = ref(1)
const teacherPageSize = ref(10)
const studentPage = ref(1)
const studentPageSize = ref(10)

const uniqueSemesterCount = computed(() => new Set(courses.value.map(item => item.semester).filter(Boolean)).size)
const paginate = (list, page, size) => list.slice((page - 1) * size, page * size)
const pagedCourses = computed(() => paginate(courses.value, coursePage.value, coursePageSize.value))
const pagedPendingUsers = computed(() => paginate(pendingUsers.value, pendingPage.value, pendingPageSize.value))
const pagedTeachers = computed(() => paginate(teachers.value, teacherPage.value, teacherPageSize.value))
const pagedStudents = computed(() => paginate(students.value, studentPage.value, studentPageSize.value))

const fetchPendingUsers = async () => {
    try {
        const res = await axios.get('/api/secretary/pending-users')
    pendingUsers.value = res.data.map(item => ({ ...item, approvedRole: item.role || 'STUDENT' }))
    } catch (e) {
        console.error(e)
    }
}

const handleApproveUser = async (row, status) => {
    try {
    await axios.post(`/api/secretary/approve-user/${row.id}`, { status, role: row.approvedRole })
        ElMessage.success(t('secretary.approve.success', { action: status === 'APPROVED' ? t('secretary.approve.btnApprove') : t('secretary.approve.btnReject') }))
        fetchPendingUsers()
    } catch (e) {
        ElMessage.error(t('secretary.serverError'))
    }
}

const fetchTeachers = async () => {
    try {
        const res = await axios.get('/api/secretary/teachers')
        teachers.value = res.data
    } catch (e) {
        console.error(e)
    }
}

const fetchStudents = async () => {
  try {
    const res = await axios.get('/api/secretary/students')
    students.value = res.data
  } catch (e) {
    console.error(e)
  }
}


const courseDialogVisible = ref(false)
const isEdit = ref(false)
const editingCourse = ref({})
const studentBindVisible = ref(false)
const boundStudents = ref([])
const allStudentsList = ref([])
const userDialogVisible = ref(false)
const userDialogMode = ref('create')
const userDialogRole = ref('TEACHER')
const userForm = ref({
  id: null,
  username: '',
  name: '',
  email: '',
  password: ''
})

const userDialogTitle = computed(() => {
  const actionKey = userDialogMode.value === 'create' ? 'createUser' : 'editUser'
  const roleKey = userDialogRole.value === 'TEACHER' ? 'teacherTitle' : 'studentTitle'
  return `${t(`secretary.management.${actionKey}`)} · ${t(`secretary.management.${roleKey}`)}`
})

const resetUserForm = () => {
  userForm.value = {
    id: null,
    username: '',
    name: '',
    email: '',
    password: ''
  }
}

const openUserDialog = (role, row = null) => {
  userDialogRole.value = role
  userDialogMode.value = row ? 'edit' : 'create'
  userForm.value = row
    ? {
        id: row.id,
        username: row.username || '',
        name: row.name || '',
        email: row.email || '',
        password: ''
      }
    : {
        id: null,
        username: '',
        name: '',
        email: '',
        password: ''
      }
  userDialogVisible.value = true
}

const submitUserForm = async () => {
  if (!userForm.value.username || !userForm.value.name) {
    ElMessage.warning(t('secretary.management.fillUserFields'))
    return
  }

  if (userDialogMode.value === 'create' && !userForm.value.password) {
    ElMessage.warning(t('secretary.management.passwordRequired'))
    return
  }

  const rolePath = userDialogRole.value === 'TEACHER' ? 'teachers' : 'students'
  const payload = {
    username: userForm.value.username,
    name: userForm.value.name,
    email: userForm.value.email,
    password: userForm.value.password
  }

  if (userDialogMode.value === 'edit' && !payload.password) {
    delete payload.password
  }

  try {
    if (userDialogMode.value === 'create') {
      await axios.post(`/api/secretary/${rolePath}`, payload)
      ElMessage.success(t('secretary.management.createSuccess'))
    } else {
      await axios.put(`/api/secretary/${rolePath}/${userForm.value.id}`, payload)
      ElMessage.success(t('secretary.management.updateSuccess'))
    }

    userDialogVisible.value = false
    resetUserForm()
    if (userDialogRole.value === 'TEACHER') {
      teacherPage.value = 1
      await fetchTeachers()
    } else {
      studentPage.value = 1
      await fetchStudents()
    }
  } catch (e) {
    ElMessage.error(t('secretary.management.userActionFailed'))
  }
}

const deleteUser = async (role, row) => {
  const rolePath = role === 'TEACHER' ? 'teachers' : 'students'
  try {
    await ElMessageBox.confirm(
      t('secretary.management.deleteConfirm', { name: row.name || row.username }),
      t('secretary.management.deleteUser'),
      {
        confirmButtonText: t('secretary.management.deleteUser'),
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    await axios.delete(`/api/secretary/${rolePath}/${row.id}`)
    ElMessage.success(t('secretary.management.deleteSuccess'))

    if (role === 'TEACHER') {
      if ((teacherPage.value - 1) * teacherPageSize.value >= Math.max(teachers.value.length - 1, 0) && teacherPage.value > 1) {
        teacherPage.value -= 1
      }
      await fetchTeachers()
    } else {
      if ((studentPage.value - 1) * studentPageSize.value >= Math.max(students.value.length - 1, 0) && studentPage.value > 1) {
        studentPage.value -= 1
      }
      await fetchStudents()
    }
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error(t('secretary.management.userActionFailed'))
    }
  }
}

const openCreateDialog = () => {
    isEdit.value = false
    editingCourse.value = {}
    courseDialogVisible.value = true
}

const openEditDialog = (row) => {
    isEdit.value = true
    editingCourse.value = { ...row }
    courseDialogVisible.value = true
}

const saveCourse = async () => {
    try {
        if (isEdit.value) {
            await axios.put(`/api/courses/${editingCourse.value.id}`, editingCourse.value)
            ElMessage.success('更新成功')
        } else {
            await axios.post('/api/courses', editingCourse.value)
            ElMessage.success('创建成功')
        }
        courseDialogVisible.value = false
        fetchCourses()
    } catch (e) {
        ElMessage.error('操作失败')
    }
}

const deleteCourse = async (id) => {
    try {
        await axios.delete(`/api/courses/${id}`)
        ElMessage.success('删除成功')
        fetchCourses()
    } catch (e) {
        ElMessage.error('删除失败')
    }
}

const openStudentBind = async (row) => {
    editingCourse.value = { ...row }
    try {
        const res = await axios.get('/api/secretary/students')
        allStudentsList.value = res.data.map(s => ({ key: s.id, label: s.name + '(' + s.username + ')' }))
        
        const bindRes = await axios.get(`/api/courses/${row.id}/students`)
        boundStudents.value = bindRes.data
        
        studentBindVisible.value = true
    } catch (e) {
        ElMessage.error('获取列表失败')
    }
}

const saveBindPeople = async () => {
    try {
        // save teacher
        await axios.put(`/api/courses/${editingCourse.value.id}`, editingCourse.value)
        // save students
        await axios.post(`/api/courses/${editingCourse.value.id}/students`, { studentIds: boundStudents.value })
        ElMessage.success('关联成功')
        studentBindVisible.value = false
        fetchCourses()
    } catch (e) {
        ElMessage.error('关联失败')
    }
}

const fetchCourses = async () => {
    try {
        const res = await axios.get('/api/secretary/courses')
        courses.value = res.data
    } catch (e) {
        console.error(e)
    }
}

const addObjectiveRow = () => {
    const nextIndex = courseObjectives.value.length + 1
    courseObjectives.value.push({
        objectiveIndex: nextIndex,
        description: '',
        regularWeight: 0.2,
        testWeight: 0.2,
        labWeight: 0.3,
        finalWeight: 0.3
    })
}

const removeObjectiveRow = (index) => {
    courseObjectives.value.splice(index, 1)
}

const createCourse = async () => {
    if (!newCourse.value.teacherId || !newCourse.value.courseCode || !newCourse.value.courseName) {
        ElMessage.warning(t('secretary.fillFields'))
        return
    }

    for (const obj of courseObjectives.value) {
        const totalWeight = obj.regularWeight + obj.testWeight + obj.labWeight + obj.finalWeight;
        if (Math.abs(totalWeight - 1.0) > 0.01) {
            ElMessage.error(t('secretary.weightError', { index: obj.objectiveIndex, total: totalWeight.toFixed(2) }))
            return
        }
    }

    try {
        const courseRes = await axios.post('/api/secretary/course/create', newCourse.value)
        const courseId = courseRes.data.courseId

        for (const obj of courseObjectives.value) {
            await axios.post('/api/secretary/course/objective', { ...obj, courseId })
        }

        ElMessage.success(t('secretary.createSuccess'))
        fetchCourses()
          newCourse.value = { courseCode: '', courseName: '', semester: '', teacherId: '', classTime: '', location: '' }
        courseObjectives.value = [{ objectiveIndex: 1, description: '', regularWeight: 0.2, testWeight: 0.2, labWeight: 0.3, finalWeight: 0.3 }]
    } catch (e) {
        ElMessage.error(t('secretary.serverError'))
    }
}

const enrollStudent = async () => {
    if (!enrollment.value.courseId || !enrollment.value.studentId) {
        ElMessage.warning(t('secretary.fillEnrollment'))
        return
    }
    try {
        await axios.post('/api/secretary/enroll', enrollment.value)
        ElMessage.success(t('secretary.enrollSuccess'))
        enrollment.value.studentId = ''
    } catch (e) {
        ElMessage.error(t('secretary.enrollError'))
    }
}

onMounted(() => {
    fetchTeachers()
  fetchStudents()
    fetchCourses()
    fetchPendingUsers()
})
</script>

<style scoped>
.secretary-dashboard {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.welcome-banner {
  background: linear-gradient(135deg, #3f2e1f 0%, #7c3aed 100%);
  border-radius: 28px;
  padding: 32px 36px;
  color: white;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 20px 40px rgba(124, 58, 237, 0.14);
}

.welcome-text h2 {
  margin: 0 0 10px 0;
  font-size: 28px;
  font-weight: 600;
}

.welcome-text p {
  margin: 0;
  font-size: 15px;
  opacity: 0.88;
  max-width: 720px;
  line-height: 1.7;
}

.welcome-stats {
  display: flex;
  gap: 16px;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  min-width: 140px;
  background: rgba(255, 255, 255, 0.14);
  padding: 18px 26px;
  border-radius: 20px;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.12);
}

.stat-value {
  font-size: 32px;
  font-weight: 700;
  line-height: 1;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 13px;
  opacity: 0.9;
}

.admin-card {
  border-radius: 24px;
  border: none;
  background: white;
}

.admin-panel {
  display: flex;
  flex-direction: column;
}

.tab-content {
  padding: 30px;
}

.planning-layout,
.enrollment-layout {
  display: flex;
  flex-direction: column;
  gap: 22px;
}

.tab-hero {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 18px;
  padding: 24px 26px;
  border-radius: 24px;
  background: linear-gradient(135deg, rgba(124, 58, 237, 0.08), rgba(245, 158, 11, 0.08));
  border: 1px solid rgba(216, 180, 254, 0.5);
}

.compact-hero h3 {
  margin: 8px 0 8px;
  font-size: 24px;
}

.compact-hero p {
  margin: 0;
  color: #6b7280;
  line-height: 1.7;
}

.tab-eyebrow {
  color: #7c3aed;
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.tab-hero-stats {
  display: flex;
  gap: 12px;
}

.mini-stat {
  min-width: 132px;
  padding: 16px 18px;
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.86);
  border: 1px solid rgba(124, 58, 237, 0.1);
}

.mini-stat strong {
  display: block;
  font-size: 24px;
  color: #111827;
}

.mini-stat span {
  display: block;
  margin-top: 8px;
  color: #6b7280;
  font-size: 13px;
  line-height: 1.5;
}

.alert-stat {
  background: rgba(254, 242, 242, 0.92);
  border-color: rgba(239, 68, 68, 0.12);
}

.form-panel,
.table-shell,
.section-surface {
  padding: 24px;
  border-radius: 24px;
  border: 1px solid rgba(233, 213, 255, 0.9);
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.98), rgba(250, 245, 255, 0.92));
}

.table-shell-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 16px;
  margin-bottom: 16px;
}

.table-shell-header h4 {
  margin: 0;
  font-size: 20px;
}

.table-shell-header p {
  margin: 8px 0 0;
  color: #6b7280;
}

.table-pagination {
  display: flex;
  justify-content: flex-end;
  margin-top: 18px;
}

.action-row {
  display: flex;
  justify-content: center;
  gap: 10px;
  flex-wrap: wrap;
}

.secretary-table {
  width: 100%;
}

.form-section-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 1px dashed #d8b4fe;
}

.with-helper {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
}

.with-helper small {
  color: #8b5cf6;
  font-size: 12px;
  font-weight: 500;
}

.title-tip {
  font-size: 12px;
  color: #909399;
  font-weight: normal;
  margin-left: 10px;
}

.objective-card {
  background: linear-gradient(180deg, #fcfbff, #f8f4ff);
  border: 1px solid #e9d5ff;
  border-radius: 18px;
  padding: 20px;
  margin-bottom: 20px;
  transition: all 0.3s;
}

.objective-card:hover {
  border-color: #c084fc;
}

.objective-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.objective-index {
  font-weight: 700;
  color: #7c3aed;
}

.obj-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.add-obj-btn {
  width: 100%;
  border-style: dashed;
  height: 46px;
  border-radius: 14px;
}

.submit-btn {
  width: 300px;
  font-weight: 600;
  letter-spacing: 2px;
}

.action-center {
  display: flex;
  justify-content: center;
  padding-top: 50px;
  padding-bottom: 50px;
}

.enroll-box {
  width: min(100%, 560px);
  background: #fff;
  border: 1px solid #ede9fe;
  border-radius: 22px;
  padding: 30px;
  box-shadow: 0 16px 30px rgba(124, 58, 237, 0.08);
}

.box-title {
  text-align: left;
  margin-top: 0;
  margin-bottom: 10px;
  color: #303133;
}

.box-subtitle {
  margin: 0 0 24px;
  color: #6b7280;
  line-height: 1.7;
}

.custom-badge {
  margin-right: 5px;
  transform: translateY(-2px);
}

.submit-action {
  margin-top: 32px;
  display: flex;
  justify-content: center;
}

.primary-submit-btn {
  min-width: 220px;
  height: 48px;
  border-radius: 14px;
}

@media (max-width: 768px) {
  .welcome-banner {
    flex-direction: column;
    align-items: flex-start;
    gap: 18px;
    padding: 26px 22px;
  }

  .welcome-stats,
  .stat-item,
  .enroll-box {
    width: 100%;
  }

  .tab-hero,
  .tab-hero-stats,
  .with-helper,
  .table-shell-header {
    flex-direction: column;
    align-items: stretch;
  }

  .form-panel,
  .table-shell,
  .section-surface {
    padding: 18px;
  }

  .table-pagination {
    justify-content: center;
    overflow-x: auto;
  }

  .tab-content {
    padding: 20px 16px;
  }
}
</style>



