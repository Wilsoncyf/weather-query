# 推送教程：发布 `weather-query` 至 GitHub
按以下步骤可将本地项目推送到空仓库 `https://github.com/Wilsoncyf/weather-query.git`。

## 1. 初始化 Git（如尚未初始化）
```bash
git init
git branch -M main
```
一次性配置作者信息：
```bash
git config user.name "Your Name"
git config user.email "you@example.com"
```

## 2. 暂存并提交全部文件
```bash
git add .
git commit -m "feat: bootstrap weather-query service"
```
提交消息建议使用 `<type>: <description>` 格式，例如 `fix: guard redis lock release`。

## 3. 添加远程仓库
```bash
git remote add origin https://github.com/Wilsoncyf/weather-query.git
```
校验：
```bash
git remote -v
```

## 4. 推送到 GitHub
```bash
git push -u origin main
```
`-u` 会设置 `origin/main` 为默认上游，之后直接 `git push` 即可。

## 5. 后续更新
有新的改动时：
```bash
git status            # 查看变更
git add <files>
git commit -m "chore: describe change"
git push
```
若远端启用了保护或需走 PR 流程，请遵循团队要求提交审核。
