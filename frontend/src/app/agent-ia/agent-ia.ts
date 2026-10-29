import { Component, OnInit, ElementRef, ViewChild, AfterViewChecked } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AiAgentService, ChatMessage, TicketStatsDto } from '../services/ai-agent.service';
import { SimpleMarkdownPipe } from '../pipes/simple-markdown.pipe';

@Component({
  selector: 'app-agent-ia',
  standalone: true,
  imports: [CommonModule, FormsModule, SimpleMarkdownPipe],
  templateUrl: './agent-ia.html',
  styleUrl: './agent-ia.scss'
})
export class AgentIaComponent implements OnInit, AfterViewChecked {
  @ViewChild('scrollAnchor') scrollAnchor?: ElementRef<HTMLDivElement>;

  stats: TicketStatsDto[] = [];
  statsLoading = true;

  summary = '';
  summaryLoading = true;
  summaryError = '';
  summaryGeneratedAt: Date | null = null;

  messages: ChatMessage[] = [];
  currentMessage = '';
  chatLoading = false;
  chatError = '';

  suggestedPrompts = [
    'Quelles entités sont en difficulté ?',
    'Top 3 des entités par volume en cours',
    'Détail des tickets pour SIOGP'
  ];

  private shouldScroll = false;

  constructor(private aiAgentService: AiAgentService) {}

  ngOnInit(): void {
    this.loadStats();
    this.loadSummary();
  }

  ngAfterViewChecked(): void {
    if (this.shouldScroll) {
      this.scrollAnchor?.nativeElement.scrollIntoView({ behavior: 'smooth' });
      this.shouldScroll = false;
    }
  }

  loadStats(): void {
    this.statsLoading = true;
    this.aiAgentService.getStats().subscribe({
      next: (res) => {
        this.stats = [...res].sort((a, b) => b.total - a.total);
        this.statsLoading = false;
      },
      error: () => { this.statsLoading = false; }
    });
  }

  loadSummary(): void {
    this.summaryLoading = true;
    this.summaryError = '';
    this.aiAgentService.getSummary().subscribe({
      next: (res) => {
        this.summary = res.message;
        this.summaryGeneratedAt = new Date();
        this.summaryLoading = false;
      },
      error: () => {
        this.summaryError = "Impossible de générer l'analyse pour l'instant.";
        this.summaryLoading = false;
      }
    });
  }

  sendPrompt(text: string): void {
    this.currentMessage = text;
    this.sendMessage();
  }

  sendMessage(): void {
    const text = this.currentMessage.trim();
    if (!text || this.chatLoading) return;

    this.messages.push({ role: 'user', content: text });
    this.currentMessage = '';
    this.chatLoading = true;
    this.chatError = '';
    this.shouldScroll = true;

    this.aiAgentService.chat(text, this.messages).subscribe({
      next: (res) => {
        this.messages.push({ role: 'assistant', content: res.message });
        this.chatLoading = false;
        this.shouldScroll = true;
      },
      error: () => {
        this.chatError = "L'agent n'a pas pu répondre. Réessaie.";
        this.chatLoading = false;
      }
    });
  }

  // Largeur relative de chaque segment de la barre de composition (0-100)
  segmentWidth(value: number, total: number): number {
    if (!total) return 0;
    return Math.round((value / total) * 1000) / 10;
  }

  healthClass(pct: number): string {
    if (pct >= 60) return 'good';
    if (pct >= 35) return 'warn';
    return 'critical';
  }
}
