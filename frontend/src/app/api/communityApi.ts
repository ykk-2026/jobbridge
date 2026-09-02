export interface ApiCommunityPost {
  id: number;
  memberId: number;
  category: 'TIP' | 'QUESTION' | 'INFO' | 'FREE';
  title: string;
  author: string;
  content: string;
  replies: number;
  views: number;
  viewCount: number;
  likeCount: number;
  status: 'ACTIVE' | 'DELETED' | 'HIDDEN';
  createdAt: string;
  updatedAt: string;
  comments: ApiCommunityComment[];
  reports: ApiCommunityReport[];
}

export interface ApiCommunityComment {
  id: number;
  postId: number;
  memberId: number;
  author: string;
  content: string;
  status: 'ACTIVE' | 'DELETED';
  createdAt: string;
  updatedAt: string;
}

export interface ApiCommunityReport {
  id: number;
  postId: number;
  reporterMemberId: number;
  reason: 'SPAM' | 'ABUSE' | 'FALSE_INFO' | 'ADVERTISEMENT' | 'ETC';
  status: 'PENDING' | 'RESOLVED' | 'REJECTED';
  createdAt: string;
}

const responseError = async (response: Response) => {
  try {
    const body = await response.json() as { detail?: string; message?: string; error?: string };
    return body.detail || body.message || body.error || `요청 실패 (${response.status})`;
  } catch {
    return `요청 실패 (${response.status})`;
  }
};

export async function getCommunityPosts(): Promise<ApiCommunityPost[]> {
  const response = await fetch('/api/community/posts');
  if (!response.ok) throw new Error(await responseError(response));
  return response.json() as Promise<ApiCommunityPost[]>;
}

export async function createCommunityPost(value: Pick<ApiCommunityPost, 'category' | 'title' | 'content'>) {
  const response = await fetch('/api/community/posts', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(value),
  });
  if (!response.ok) throw new Error(await responseError(response));
  return response.json() as Promise<ApiCommunityPost>;
}

export async function incrementCommunityPostViews(id: string) {
  const response = await fetch(`/api/community/posts/${encodeURIComponent(id)}/views`, { method: 'POST' });
  if (!response.ok) throw new Error(await responseError(response));
  return response.json() as Promise<ApiCommunityPost>;
}

export async function deleteCommunityPost(id: string) {
  const response = await fetch(`/api/community/posts/${encodeURIComponent(id)}`, { method: 'DELETE' });
  if (!response.ok) throw new Error(await responseError(response));
}

export async function createCommunityComment(postId: string, content: string) {
  const response = await fetch(`/api/community/posts/${encodeURIComponent(postId)}/comments`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ content }),
  });
  if (!response.ok) throw new Error(await responseError(response));
  return response.json() as Promise<ApiCommunityComment>;
}

export async function deleteCommunityComment(postId: string, commentId: string) {
  const response = await fetch(`/api/community/posts/${encodeURIComponent(postId)}/comments/${encodeURIComponent(commentId)}`, { method: 'DELETE' });
  if (!response.ok) throw new Error(await responseError(response));
}

export async function reportCommunityPost(postId: string) {
  const response = await fetch(`/api/community/posts/${encodeURIComponent(postId)}/reports`, { method: 'POST' });
  if (!response.ok) throw new Error(await responseError(response));
  return response.json() as Promise<ApiCommunityReport>;
}
